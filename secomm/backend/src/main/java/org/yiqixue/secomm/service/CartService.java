package org.yiqixue.secomm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yiqixue.secomm.dto.AddToCartRequest;
import org.yiqixue.secomm.dto.CartDTO;
import org.yiqixue.secomm.dto.CartItemDTO;
import org.yiqixue.secomm.dto.UpdateCartItemRequest;
import org.yiqixue.secomm.entity.Cart;
import org.yiqixue.secomm.entity.CartItem;
import org.yiqixue.secomm.entity.Customer;
import org.yiqixue.secomm.entity.Product;
import org.yiqixue.secomm.entity.User;
import org.yiqixue.secomm.exception.ResourceNotFoundException;
import org.yiqixue.secomm.exception.BusinessException;
import org.yiqixue.secomm.mapper.CartMapper;
import org.yiqixue.secomm.repository.CartRepository;
import org.yiqixue.secomm.repository.CartItemRepository;
import org.yiqixue.secomm.repository.ProductRepository;
import org.yiqixue.secomm.repository.CustomerRepository;
import org.yiqixue.secomm.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 购物车服务层
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class  CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    private Customer resolveOrCreateCustomer(Long userId) {
        return customerRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findByIdWithRoles(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

                    boolean hasCustomerRole = user.hasCustomerRole();
                    if (!hasCustomerRole) {
                        throw new BusinessException("当前用户未分配客户角色，无法使用购物车");
                    }

                    if (!User.ApprovalStatus.APPROVED.equals(user.getApprovalStatus())) {
                        throw new BusinessException("客户账户尚未审批，请联系管理员");
                    }

                    Customer customer = Customer.fromApprovedUser(user);
                    return customerRepository.save(customer);
                });
    }

    /**
     * 获取用户的购物车
     */
    @Transactional
    public CartDTO getCartByCustomerId(Long userId) {
        log.info("获取用户购物车 - 用户ID: {}", userId);

        Customer customer = resolveOrCreateCustomer(userId);

        Optional<Cart> cartOpt = cartRepository.findByCustomerIdAndStatus(
                customer.getId(), Cart.CartStatus.ACTIVE);

        if (cartOpt.isEmpty()) {
            // 如果没有购物车，创建一个空的
            Cart newCart = Cart.builder()
                    .customerId(customer.getId())
                    .status(Cart.CartStatus.ACTIVE)
                    .build();
            newCart = cartRepository.save(newCart);
            return cartMapper.toDTO(newCart);
        }

        return cartMapper.toDTO(cartOpt.get());
    }

    /**
     * 添加商品到购物车
     */
    @Transactional
    public CartItemDTO addToCart(Long userId, AddToCartRequest request) {
        log.info("添加商品到购物车 - 用户ID: {}, 商品ID: {}, 数量: {}", 
                userId, request.getProductId(), request.getQuantity());

        Customer customer = resolveOrCreateCustomer(userId);

        // 验证商品是否存在
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "id", request.getProductId()));

        // 检查库存
        if (product.getUnitsInStock() < request.getQuantity()) {
            throw new BusinessException("商品库存不足，当前库存: " + product.getUnitsInStock());
        }

        // 获取或创建购物车
        Cart cart = cartRepository.findByCustomerIdAndStatus(
                customer.getId(), Cart.CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .customerId(customer.getId())
                            .status(Cart.CartStatus.ACTIVE)
                            .build();
                    return cartRepository.save(newCart);
                });

        // 检查商品是否已在购物车中
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), request.getProductId());

        CartItem cartItem;
        if (existingItem.isPresent()) {
            // 更新数量
            cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            
            // 再次检查库存
            if (product.getUnitsInStock() < newQuantity) {
                throw new BusinessException("商品库存不足，当前库存: " + product.getUnitsInStock());
            }
            
            cartItem.setQuantity(newQuantity);
            cartItem = cartItemRepository.save(cartItem);
        } else {
            // 创建新的购物车项
            cartItem = CartItem.builder()
                    .cartId(cart.getId())
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .unitPrice(product.getUnitPrice())
                    .build();
            cartItem = cartItemRepository.save(cartItem);
        }

        // 更新购物车总计
        updateCartTotals(cart.getId());

        return cartMapper.toCartItemDTO(cartItem);
    }

    /**
     * 更新购物车商品项数量
     */
    @Transactional
    public CartItemDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        log.info("更新购物车商品项 - 用户ID: {}, 商品项ID: {}, 新数量: {}", 
                userId, cartItemId, request.getQuantity());

        Customer customer = resolveOrCreateCustomer(userId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CartItem", "id", cartItemId));

        // 验证购物车是否属于该用户
        CartItem finalCartItem = cartItem;
        Cart cart = cartRepository.findById(cartItem.getCartId())

                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart", "id",  finalCartItem.getCartId()));

        if (!cart.getCustomerId().equals(customer.getId())) {
            throw new BusinessException("无权限操作此购物车");
        }

        // 检查库存
        CartItem finalCartItem1 = cartItem;
        Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product", "id", finalCartItem1.getProductId()));

        if (product.getUnitsInStock() < request.getQuantity()) {
            throw new BusinessException("商品库存不足，当前库存: " + product.getUnitsInStock());
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem = cartItemRepository.save(cartItem);

        // 更新购物车总计
        updateCartTotals(cart.getId());

        return cartMapper.toCartItemDTO(cartItem);
    }

    /**
     * 从购物车移除商品
     */
    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        log.info("从购物车移除商品 - 用户ID: {}, 商品项ID: {}", userId, cartItemId);

        Customer customer = resolveOrCreateCustomer(userId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CartItem", "id", cartItemId));

        // 验证购物车是否属于该用户
        Cart cart = cartRepository.findById(cartItem.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart", "id", cartItem.getCartId()));

        if (!cart.getCustomerId().equals(customer.getId())) {
            throw new BusinessException("无权限操作此购物车");
        }

        Long cartId = cart.getId();
        
        // 先删除购物车项
        cartItemRepository.delete(cartItem);
        
        // 刷新实体管理器，确保删除操作已提交
        cartItemRepository.flush();

        // 重新获取购物车并更新总计
        updateCartTotals(cartId);
    }

    /**
     * 清空购物车
     */
    @Transactional
    public void clearCart(Long userId) {
        log.info("清空购物车 - 用户ID: {}", userId);

        Customer customer = resolveOrCreateCustomer(userId);

        Optional<Cart> cartOpt = cartRepository.findByCustomerIdAndStatus(
                customer.getId(), Cart.CartStatus.ACTIVE);

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cartItemRepository.deleteByCartId(cart.getId());
            
            // 重置购物车总计
            cart.setTotalPrice(java.math.BigDecimal.ZERO);
            cart.setTotalQuantity(0);
            cartRepository.save(cart);
        }
    }

    /**
     * 获取购物车商品项列表
     */
    @Transactional
    public List<CartItemDTO> getCartItems(Long userId) {
        log.info("获取购物车商品项 - 用户ID: {}", userId);

        Customer customer = resolveOrCreateCustomer(userId);

        Optional<Cart> cartOpt = cartRepository.findByCustomerIdAndStatus(
                customer.getId(), Cart.CartStatus.ACTIVE);

        if (cartOpt.isEmpty()) {
            return List.of();
        }

        List<CartItem> cartItems = cartItemRepository.findByCartIdWithProduct(cartOpt.get().getId());
        return cartMapper.toCartItemDTOList(cartItems);
    }

    /**
     * 更新购物车总计
     */
    private void updateCartTotals(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));

        // 重新查询购物车项，避免使用缓存的已删除实体
        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);
        
        if (cartItems.isEmpty()) {
            cart.setTotalPrice(java.math.BigDecimal.ZERO);
            cart.setTotalQuantity(0);
        } else {
            java.math.BigDecimal totalPrice = cartItems.stream()
                    .map(CartItem::getSubtotal)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            Integer totalQuantity = cartItems.stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();
            
            cart.setTotalPrice(totalPrice);
            cart.setTotalQuantity(totalQuantity);
        }
        
        cartRepository.save(cart);
    }
}
