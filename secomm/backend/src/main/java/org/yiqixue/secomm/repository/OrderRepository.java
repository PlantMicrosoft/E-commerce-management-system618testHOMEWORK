package org.yiqixue.secomm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.Order;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 根据顾客ID查找订单（分页）
     */
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * 根据订单状态查找订单（分页）
     */
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    /**
     * 根据日期范围查找订单
     */
    @Query("SELECT o FROM Order o WHERE o.dateCreated BETWEEN :startDate AND :endDate")
    Page<Order> findOrdersByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * 查找顾客在指定日期范围内的订单
     */
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId " +
            "AND o.dateCreated BETWEEN :startDate AND :endDate")
    Page<Order> findCustomerOrdersByDateRange(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * 根据订单号查找订单
     */
    Optional<Order> findByOrderNumber(String orderNumber);
}
