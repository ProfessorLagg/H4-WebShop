package webshop.api.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webshop.api.model.*;
import webshop.api.repository.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {
		@Autowired
		private CategoryRepository repository;

		private ResponseEntity<Object> notFound() {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("could not find category");
		}
		private ResponseEntity<Object> notFound(Integer id) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("could not find category with id = " + id.toString());
		}

		@GetMapping
		public ResponseEntity<Object> getAll() {
				List<Category> items = repository.findAll();
				return ResponseEntity.ok(items);
		}

		@GetMapping("/{id}")
		public ResponseEntity<Object> get(@PathVariable Integer id) {
				Optional<Category> categoryOptional = repository.findById(id);
				if (categoryOptional.isPresent()) {
						Category category = categoryOptional.get();
						return ResponseEntity.ok(category);
				} else {
						return notFound(id);
				}
		}

		// TODO AUTHENTICATION
		@PostMapping
		public ResponseEntity<Object> create(@RequestBody Category inCategory) {
				Integer inputId = inCategory.getId();
				if (inCategory.getId() != null && repository.existsById(inputId)) {
						return ResponseEntity.badRequest().body("there already exists an item with id = " + inputId.toString());
				}
				Category outCategory = repository.save(inCategory);
				return ResponseEntity.ok(outCategory);
		}

		// TODO AUTHENTICATION
		@PutMapping("/{id}")
		public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody Category inCategory) {
				Optional<Category> categoryOptional = repository.findById(id);
				if (categoryOptional.isPresent()) {
						Category category = categoryOptional.get();
						category.cloneFrom(inCategory, false);
						Category outCategory = repository.save(category);
						return ResponseEntity.ok(outCategory);
				} else { return notFound(id); }
		}

		// TODO AUTHENTICATION
		@DeleteMapping("/{id}")
		public ResponseEntity<Object> deleteById(@PathVariable Integer id) {
				Optional<Category> categoryOptional = repository.findById(id);
				if (categoryOptional.isPresent()) {
						Category category = categoryOptional.get();
						repository.delete(category);
						return ResponseEntity.ok(category);
				} else { return notFound(id); }
		}
}
