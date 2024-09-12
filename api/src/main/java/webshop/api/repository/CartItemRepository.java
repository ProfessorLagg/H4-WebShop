package webshop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webshop.api.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> { }
