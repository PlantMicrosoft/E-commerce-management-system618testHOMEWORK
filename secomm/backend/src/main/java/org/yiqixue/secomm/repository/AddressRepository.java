package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    // 基础的CRUD操作已由JpaRepository提供
    // 如果需要特殊查询，可以在这里添加
}