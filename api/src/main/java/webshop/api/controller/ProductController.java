package webshop.api.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webshop.api.GlobalUtils;
import webshop.api.model.*;
import webshop.api.repository.*;

import java.util.*;

@RestController
@RequestMapping("/product")
public class ProductController {
		@Autowired
		private ProductRepository repository;

		// (C)RUD
		@PostMapping
		public ResponseEntity<Object> create(@RequestBody Product in) {
				if (in.id != null && repository.existsById(in.id)) { return GlobalUtils.alreadyExists("product", in.id); }
				Product out = repository.save(in);
				return ResponseEntity.ok(out);
		}

		// C(R)UD
		@GetMapping
		public List<Product> readAll() { return repository.findAll(); }
		@GetMapping("/{id}")
		public ResponseEntity<Object> read(@PathVariable Integer id) {
				Optional<Product> optional = repository.findById(id);
				if (optional.isPresent()) {
						Product product = optional.get();
						return ResponseEntity.ok(product);
				} else {
						return GlobalUtils.notFound("product", id);
				}
		}

		// CR(U)D
		@PutMapping("/{id}")
		public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody Product in) {
				Optional<Product> optional = repository.findById(id);
				if (optional.isPresent()) {
						Product product = optional.get();
						product.cloneFrom(in, false);
						return ResponseEntity.ok(product);
				} else {
						return GlobalUtils.notFound("product", id);
				}
		}

		// CRU(D)
		@DeleteMapping("/{id}")
		public ResponseEntity<Object> delete(@PathVariable Integer id) {
				Optional<Product> optional = repository.findById(id);
				if (optional.isPresent()) {
						Product product = optional.get();
						repository.delete(product);
						return ResponseEntity.ok(product);
				} else {
						return GlobalUtils.notFound("product", id);
				}
		}
}
