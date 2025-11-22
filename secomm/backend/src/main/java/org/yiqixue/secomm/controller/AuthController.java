package org.yiqixue.secomm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.yiqixue.secomm.dto.ApiResponse;
import org.yiqixue.secomm.dto.auth.JwtResponse;
import org.yiqixue.secomm.dto.auth.LoginRequest;
import org.yiqixue.secomm.dto.auth.SignupRequest;
import org.yiqixue.secomm.entity.Role;
import org.yiqixue.secomm.entity.User;
import org.yiqixue.secomm.repository.RoleRepository;
import org.yiqixue.secomm.repository.UserRepository;
import org.yiqixue.secomm.security.JwtUtils;
import org.yiqixue.secomm.security.UserPrincipal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

//        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
//            return ResponseEntity.badRequest()
//                    .body(ApiResponse.error(400, "您已经登录，请不要重复登录。"));
//        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        JwtResponse jwtResponse = new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getRealName(),
                userDetails.getPhone(),

                User.Gender.M, // You might want to get this from user entity
                roles);

        return ResponseEntity.ok(ApiResponse.success("登录成功", jwtResponse));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        log.info("用户注册请求: {}, 角色: {}", signUpRequest.getUsername(), signUpRequest.getRoleCode());

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "用户名已存在"));
        }

        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "手机号已存在"));
        }

        // Create new user
        User.UserBuilder userBuilder = User.builder()
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .realName(signUpRequest.getRealName())
                .phone(signUpRequest.getPhone())
                .gender(signUpRequest.getGender())
                .status(User.UserStatus.ACTIVE);

        // 如果注册为客户角色，设置审批状态为待审批
        if ("ROLE_USER".equals(signUpRequest.getRoleCode())) {
            userBuilder.approvalStatus(User.ApprovalStatus.PENDING);
        }

        User user = userBuilder.build();

        // Set role
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByRoleCode(signUpRequest.getRoleCode())
                .orElseThrow(() -> new RuntimeException("角色未找到: " + signUpRequest.getRoleCode()));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        String message = "ROLE_CUSTOMER".equals(signUpRequest.getRoleCode()) 
                ? "客户注册成功，请等待管理员审批" 
                : "用户注册成功";

        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        log.info("获取所有角色列表");
        
        List<Role> roles = roleRepository.findAll();
        
        return ResponseEntity.ok(ApiResponse.success(roles));
    }
}
