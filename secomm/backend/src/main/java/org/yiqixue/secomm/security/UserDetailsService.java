package org.yiqixue.secomm.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yiqixue.secomm.entity.User;
import org.yiqixue.secomm.repository.UserRepository;

/**
 * 用户认证服务 - 只处理User实体的登录认证
 * 不涉及Customer业务逻辑
 */
@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 检查用户基本状态
        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }

        if (!user.isAccountNonLocked()) {
            throw new UsernameNotFoundException("User account is locked: " + username);
        }

        // 对于Customer角色，检查审批状态
        if (user.hasCustomerRole()) {
            if (user.needsApproval()) {
                throw new UsernameNotFoundException("Customer account is pending approval: " + username);
            }
            if (User.ApprovalStatus.REJECTED.equals(user.getApprovalStatus())) {
                throw new UsernameNotFoundException("Customer account has been rejected: " + username);
            }
        }

        return UserPrincipal.create(user);
    }
}