package webshop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webshop.api.model.*;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {
}
