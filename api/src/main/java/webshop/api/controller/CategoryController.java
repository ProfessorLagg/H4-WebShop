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
				List<Category> items = repository.findAll();
				JSONArray result = new JSONArray();
				for (Category category : items) {
						JSONObject itemJson = category.toJSON();
						result.put(itemJson);
				}
				return okJSON(result);
		}

		@GetMapping("/{id}")
		public ResponseEntity<Object> get(@PathVariable Integer id) {
				Optional<Category> itemOptional = repository.findById(id);
				if (itemOptional.isPresent()) {
						return okJSON(itemOptional.get().toJSON());
				} else {
						return notFound(id);
				}
		}

		// TODO AUTHENTICATION
		@PostMapping
		public ResponseEntity<Object> create(@RequestBody JSONObject inputJson) {
				Category inCategory = new Category();
				inCategory.parseJSON(inputJson);
				Integer inputId = inCategory.getId();
				if (inCategory.getId() != null && repository.existsById(inputId)) {
						return ResponseEntity.badRequest().body("there already exists an item with id = " + inputId.toString());
				}
				Category outCategory = repository.save(inCategory);
				return okJSON(outCategory.toJSON());
		}

		// TODO AUTHENTICATION
		@PutMapping("/{id}")
		public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody JSONObject categoryDetails) {
				Optional<Category> categoryOptional = repository.findById(id);
				if (categoryOptional.isPresent()) {
						categoryDetails.remove("id"); // The route id decides the id in DB to update
						Category category = categoryOptional.get();
						category.parseJSON(categoryDetails);
						repository.save(category);
						return okJSON(category.toJSON());
				} else { return notFound(id); }
		}

		// TODO AUTHENTICATION
		@DeleteMapping("/{id}")
		public ResponseEntity<Object> deleteById(@PathVariable Integer id) {
				Optional<Category> categoryOptional = repository.findById(id);
				if (categoryOptional.isPresent()) {
						Category category = categoryOptional.get();
						repository.delete(category);
						return okJSON(category.toJSON());
				} else { return notFound(id); }
		}
}
