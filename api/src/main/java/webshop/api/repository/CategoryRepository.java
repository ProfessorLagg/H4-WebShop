package webshop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webshop.api.model.*;

public interface CategoryRepository extends JpaRepository<Category, Integer> { }
