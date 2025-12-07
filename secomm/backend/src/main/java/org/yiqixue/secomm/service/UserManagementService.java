package org.yiqixue.secomm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yiqixue.secomm.dto.user.UserApprovalRequest;
import org.yiqixue.secomm.entity.Customer;
import org.yiqixue.secomm.entity.Role;
import org.yiqixue.secomm.entity.User;
import org.yiqixue.secomm.exception.ResourceNotFoundException;
import org.yiqixue.secomm.repository.CustomerRepository;
// import org.yiqixue.secomm.repository.RoleRepository;
import org.yiqixue.secomm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    // private final RoleRepository roleRepository;

    /**
     * 获取待审批的用户列表
     */
    public List<User> getPendingUsers() {
        return userRepository.findByApprovalStatus(User.ApprovalStatus.PENDING);
    }

    /**
     * 获取所有用户列表
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 审批用户注册
     */
    @Transactional
    public void approveUser(Long userId, UserApprovalRequest request, Long adminId) {
        log.info("审批用户: 用户ID={}, 审批状态={}", userId, request.getApprovalStatus());

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // 获取用户角色（不设置到user对象上）
        Set<Role> userRoles = user.getRoles();
        if (userRoles == null || userRoles.isEmpty()) {
            userRoles = userRepository.getRolesByUserId(userId);
            log.info("手动加载用户角色: 用户ID={}, 角色数量={}", userId, userRoles.size());
        }

        // 调试日志：打印用户角色信息
        log.info("用户角色信息: 用户ID={}, 角色数量={}", userId, userRoles.size());
        userRoles.forEach(role -> 
            log.info("角色详情: ID={}, Code={}, Name={}", role.getId(), role.getRoleCode(), role.getRoleName()));

        // 更新审批信息
        user.setApprovalStatus(request.getApprovalStatus());
        user.setApprovalTime(LocalDateTime.now());
        user.setApprovedBy(adminId);
        user.setApprovalNotes(request.getApprovalNotes());

        userRepository.save(user);

        // 检查是否有Customer角色
        boolean hasCustomerRole = userRoles.stream()
                .anyMatch(role -> "ROLE_USER".equals(role.getRoleCode()));
        log.info("用户是否有ROLE_USER角色: {}", hasCustomerRole);

        // 如果审批通过且是客户角色，创建Customer记录
        if (User.ApprovalStatus.APPROVED.equals(request.getApprovalStatus()) && hasCustomerRole) {
            
            // 检查是否已存在Customer记录
            if (!customerRepository.existsByUserId(userId)) {
                Customer customer = Customer.fromApprovedUser(user);
                customerRepository.save(customer);
                log.info("已为用户创建Customer记录: 用户ID={}, 客户ID={}", userId, customer.getId());
            } else {
                log.info("用户已存在Customer记录: 用户ID={}", userId);
            }
        } else {
            log.info("不满足创建Customer记录的条件: 审批状态={}, 有ROLE_USER角色={}", 
                    request.getApprovalStatus(), hasCustomerRole);
        }
    }

    /**
     * 根据用户ID获取用户详细信息
     */
    public User getUserById(Long userId) {
        log.info("获取用户详细信息 - 用户ID: {}", userId);
        
        return userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    /**
     * 更新用户基础信息
     */
    @Transactional
    public User updateUser(Long userId, org.yiqixue.secomm.dto.user.UserUpdateRequest request) {
        log.info("更新用户信息: 用户ID={}, 请求={}", userId, request);

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // 更新真实姓名
        if (request.getRealName() != null && !request.getRealName().isBlank()) {
            user.setRealName(request.getRealName().trim());
        }

        // 更新邮箱（系统以 username 作为邮箱）
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equals(user.getUsername()) && userRepository.existsByUsername(newEmail)) {
                throw new IllegalArgumentException("邮箱已存在: " + newEmail);
            }
            user.setUsername(newEmail);
        }

        // 更新状态
        if (request.getActive() != null) {
            user.setStatus(request.getActive() ? User.UserStatus.ACTIVE : User.UserStatus.INACTIVE);
        }

        // 更新审批状态（仅对 Customer 角色有效）
        if (request.getApprovalStatus() != null) {
            user.setApprovalStatus(request.getApprovalStatus());

            // 如审批通过且为客户角色，则补建 Customer 记录
            boolean hasCustomerRole = user.getRoles() != null && user.getRoles().stream()
                    .anyMatch(role -> "ROLE_USER".equals(role.getRoleCode()));
            if (hasCustomerRole && User.ApprovalStatus.APPROVED.equals(request.getApprovalStatus())) {
                if (!customerRepository.existsByUserId(userId)) {
                    Customer customer = Customer.fromApprovedUser(user);
                    customerRepository.save(customer);
                    log.info("审批通过后已创建Customer记录: 用户ID={}, 客户ID={}", userId, customer.getId());
                }
            }
        }

        // // 更新角色（单选）
        // if (request.getRoleCode() != null && !request.getRoleCode().isBlank()) {
        //     Role role = roleRepository.findByRoleCode(request.getRoleCode())
        //             .orElseThrow(() -> new ResourceNotFoundException("Role", "roleCode", request.getRoleCode()));

        //     if (user.getRoles() == null) {
        //         user.setRoles(new java.util.HashSet<>());
        //     } else {
        //         user.getRoles().clear();
        //     }
        //     user.getRoles().add(role);

        //     if (User.ApprovalStatus.APPROVED.equals(user.getApprovalStatus()) && "ROLE_USER".equals(role.getRoleCode())) {
        //         if (!customerRepository.existsByUserId(userId)) {
        //             Customer customer = Customer.fromApprovedUser(user);
        //             customerRepository.save(customer);
        //             log.info("变更角色后已创建Customer记录: 用户ID={}, 客户ID={}", userId, customer.getId());
        //         }
        //     }
        // }

        return userRepository.save(java.util.Objects.requireNonNull(user));
    }
}
