package webshop.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webshop.api.Utils;
import webshop.api.model.Product;
import webshop.api.repository.ProductRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {
		@Autowired
		private ProductRepository repository;

		// (C)RUD
		@PostMapping
		public ResponseEntity<Object> create(@RequestBody Product in) {
				if (in.id != null && repository.existsById(in.id)) { return Utils.alreadyExists("product", in.id); }
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
						return Utils.notFound("product", id);
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
						return Utils.notFound("product", id);
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
						return Utils.notFound("product", id);
				}
		}

		@GetMapping("/bycat/{categoryId}")
		public ResponseEntity<Object> getProductsInCategory(@PathVariable Integer categoryId) {
				// TODO Figure out how you actually do a "SELECT * WHERE" query. This is why we hand-roll the SQL
				List<Product> products = repository.findAll().stream().filter(p -> Objects.equals(p.category.id, categoryId)).toList();
				if (products.isEmpty()) {
						return Utils.notFound("products in category", categoryId);
				} else {
						return ResponseEntity.ok(products);
				}
		}
}
