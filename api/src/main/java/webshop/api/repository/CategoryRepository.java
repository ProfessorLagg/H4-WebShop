package webshop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webshop.api.model.Category;
import webshop.api.model.Product;

public interface CategoryRepository extends JpaRepository<Category, Integer> { }
