package com.crisado.delivery.repository;

import com.crisado.delivery.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<Product, Integer> {
}