package webshop.api.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webshop.api.GlobalUtils;
import webshop.api.model.*;
import webshop.api.repository.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {
		@Autowired
		private CategoryRepository repository;

		// (C)RUD
		@PostMapping
		public ResponseEntity<Object> create(@RequestBody Category in) {
				if (in.id != null && repository.existsById(in.id)) { return GlobalUtils.alreadyExists("category", in.id); }
				Category out = repository.save(in);
				return ResponseEntity.ok(out);
		}

		// C(R)UD
		@GetMapping
		public List<Category> readAll() { return repository.findAll(); }
		@GetMapping("/{id}")
		public ResponseEntity<Object> read(@PathVariable Integer id) {
				Optional<Category> optional = repository.findById(id);
				if (optional.isPresent()) {
						Category category = optional.get();
						return ResponseEntity.ok(category);
				} else {
						return GlobalUtils.notFound("category", id);
				}
		}

		// CR(U)D
		@PutMapping("/{id}")
		public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody Category in) {
				Optional<Category> optional = repository.findById(id);
				if (optional.isPresent()) {
						Category category = optional.get();
						category.cloneFrom(in, false);
						return ResponseEntity.ok(category);
				} else {
						return GlobalUtils.notFound("category", id);
				}
		}

		// CRU(D)
		@DeleteMapping("/{id}")
		public ResponseEntity<Object> delete(@PathVariable Integer id) {
				Optional<Category> optional = repository.findById(id);
				if (optional.isPresent()) {
						Category category = optional.get();
						repository.delete(category);
						return ResponseEntity.ok(category);
				} else {
						return GlobalUtils.notFound("category", id);
				}
		}
}
