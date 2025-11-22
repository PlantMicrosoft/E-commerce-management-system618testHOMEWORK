package org.yiqixue.secomm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.yiqixue.secomm.dto.ApiResponse;
import org.yiqixue.secomm.dto.CheckoutRequest;
import org.yiqixue.secomm.dto.OrderDTO;
import org.yiqixue.secomm.security.UserPrincipal;
import org.yiqixue.secomm.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    /**
     * 结算购物车，创建订单
     */
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderDTO>> checkout(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CheckoutRequest request) {
        
        log.info("API调用 - 结算: 用户ID={}, 总金额={}", 
                userPrincipal.getId(), request.getTotalAmount());

        OrderDTO order = orderService.createOrderFromCart(userPrincipal.getId(), request);

        return ResponseEntity.ok(ApiResponse.success("订单创建成功", order));
    }
}