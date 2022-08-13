package com.sumit.os.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sumit.os.api.entity.Order;

public interface OrderRepository extends JpaRepository<Order,Integer> {
}
