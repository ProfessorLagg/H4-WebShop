package webshop.api.controller;

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
		private static ResponseEntity<Object> okJSON(JSONObject json) {
				return ResponseEntity
								.status(HttpStatus.OK)
								.contentType(MediaType.APPLICATION_JSON)
								.body(json.toString());
		}
		private static ResponseEntity<Object> okJSON(JSONArray json) {
				return ResponseEntity
								.status(HttpStatus.OK)
								.contentType(MediaType.APPLICATION_JSON)
								.body(json.toString());
		}

		@GetMapping
		public ResponseEntity<Object> getAll() {
				List<SubCategory> items = repository.findAll();
				JSONArray result = new JSONArray();
				for (SubCategory item : items) {
						result.put(item.toJSON());
				}
				return okJSON(result);
		}

		@GetMapping("/{id}")
		public ResponseEntity<Object> get(@PathVariable Integer id) {
				Optional<SubCategory> categoryOptional = repository.findById(id);
				if (categoryOptional.isPresent()) {
						return okJSON(categoryOptional.get().toJSON());
				} else {
						return notFound(id);
				}
		}

		// TODO AUTHENTICATION
		@PostMapping
		public ResponseEntity<Object> create(@RequestBody JSONObject inputJson) {
				SubCategory inputCategory = new SubCategory();
				inputCategory.parseJSON(inputJson);
				Integer inputId = inputCategory.getId();
				if (inputCategory.getId() != null && repository.existsById(inputId)) {
						return ResponseEntity.badRequest().body("there already exists an item with id = " + inputId.toString());
				}
				SubCategory category = repository.save(inputCategory);
				return okJSON(category.toJSON());
		}

		// TODO AUTHENTICATION
		@PutMapping("/{id}")
		public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody JSONObject categoryDetails) {
				Optional<SubCategory> categoryOptional = repository.findById(id);
				if (categoryOptional.isPresent()) {
						categoryDetails.remove("id"); // The route id decides the id in DB to update
						SubCategory category = categoryOptional.get();
						category.parseJSON(categoryDetails);
						repository.save(category);
						return okJSON(category.toJSON());
				} else { return notFound(id); }
		}

		// TODO AUTHENTICATION
		@DeleteMapping("/{id}")
		public ResponseEntity<Object> deleteById(@PathVariable Integer id) {
				Optional<SubCategory> categoryOptional = repository.findById(id);
				if (categoryOptional.isPresent()) {
						SubCategory category = categoryOptional.get();
						repository.delete(category);
						return okJSON(category.toJSON());
				} else { return notFound(id); }
		}
}
