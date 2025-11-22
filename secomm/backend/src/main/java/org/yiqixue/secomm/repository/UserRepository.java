package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.Role;
import org.yiqixue.secomm.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByPhone(String phone);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByPhone(String phone);

    List<User> findByApprovalStatus(User.ApprovalStatus approvalStatus);
    
    @Query("SELECT u.roles FROM User u WHERE u.username = :username")
    Set<Role> getRolesByUsername(@Param("username") String username);

    @Query("SELECT u.roles FROM User u WHERE u.id = :userId")
    Set<Role> getRolesByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :userId")
    Optional<User> findByIdWithRoles(@Param("userId") Long userId);
}
