package webshop.api.repository;

import org.springframework.data.jpa.repository.*;
import webshop.api.model.*;

public interface ProductRepository extends JpaRepository<Product, Integer> { }
