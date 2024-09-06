package webshop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webshop.api.model.*;

public interface ShopItemRepository extends JpaRepository<ShopItem, Integer> {
}
