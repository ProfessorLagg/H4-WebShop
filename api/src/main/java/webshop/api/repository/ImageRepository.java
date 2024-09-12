package webshop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webshop.api.model.Image;

public interface ImageRepository extends JpaRepository<Image, String> { }
