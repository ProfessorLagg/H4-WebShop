package webshop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webshop.api.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> { }
