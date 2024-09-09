package webshop.api.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import org.hibernate.mapping.Subclass;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webshop.api.model.Category;
import webshop.api.model.SubCategory;
import webshop.api.repository.SubCategoryRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/subcategory")
public class SubCategoryController {
		@Autowired
		private SubCategoryRepository repository;

		private ResponseEntity<Object> notFound() {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("could not find sub-category");
		}
		private ResponseEntity<Object> notFound(Integer id) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
								.body("could not find sub-category with id = " + id.toString());
		}

		@GetMapping
		public ResponseEntity<Object> getAll() {
				List<SubCategory> items = repository.findAll();
				return ResponseEntity.ok(items);
		}

		@GetMapping("/{id}")
		public ResponseEntity<Object> get(@PathVariable Integer id) {
				Optional<SubCategory> subcategoryOptional = repository.findById(id);
				if (subcategoryOptional.isPresent()) {
						SubCategory subCategory = subcategoryOptional.get();
						return ResponseEntity.ok(subCategory);
				} else {
						return notFound(id);
				}
		}

		// TODO AUTHENTICATION
		@PostMapping
		public ResponseEntity<Object> create(@RequestBody SubCategory inSubcategory) {
				@Nullable
				Integer inputId = inSubcategory.getId();
				if (inputId != null && repository.existsById(inputId)) {
						return ResponseEntity.badRequest().body("there already exists an item with id = " + inputId.toString());
				}
				SubCategory outSubcategory = repository.save(inSubcategory);
				return ResponseEntity.ok(outSubcategory);
		}

		// TODO AUTHENTICATION
		@PutMapping("/{id}")
		public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody SubCategory inSubcategory) {
				Optional<SubCategory> categoryOptional = repository.findById(id);
				if (categoryOptional.isPresent()) {
						SubCategory category = categoryOptional.get();
						category.cloneFrom(inSubcategory, false);
						repository.save(category);
						return ResponseEntity.ok(category);
				} else { return notFound(id); }
		}

		// TODO AUTHENTICATION
		@DeleteMapping("/{id}")
		public ResponseEntity<Object> deleteById(@PathVariable Integer id) {
				Optional<SubCategory> categoryOptional = repository.findById(id);
				if (categoryOptional.isPresent()) {
						SubCategory category = categoryOptional.get();
						repository.delete(category);
						return ResponseEntity.ok(category);
				} else { return notFound(id); }
		}
}
