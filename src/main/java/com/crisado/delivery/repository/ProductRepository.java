package com.crisado.delivery.repository;

import com.crisado.delivery.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface ProductRepository extends Repository<Product, Integer> {
    
    List<Product> findAll();
    
    Optional<Product> findById(int id);
    
}