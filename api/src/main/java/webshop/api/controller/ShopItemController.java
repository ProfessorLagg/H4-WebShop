package webshop.api.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webshop.api.model.ShopItem;
import webshop.api.model.SubCategory;
import webshop.api.repository.ShopItemRepository;
import webshop.api.repository.SubCategoryRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/shopitem")
public class ShopItemController {
		@Autowired
		private ShopItemRepository repository;

		@Autowired
		private SubCategoryRepository subCategoryRepository;

		private ResponseEntity<Object> notFound() {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("could not find shop item");
		}

		private ResponseEntity<Object> notFound(Integer id) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("could not find shop item with id = " + id.toString());
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

		@GetMapping("/schema")
		public ResponseEntity<Object> getSchema() {
				ShopItem item = ShopItem.DEFAULT_ITEM;

				List<SubCategory> subCategories = this.subCategoryRepository.findAll();
				if (!subCategories.isEmpty()) { item.setSubCategory(subCategories.getFirst()); }

				return okJSON(item.toJSON());
		}

		@GetMapping
		public ResponseEntity<Object> getAll() {
				List<ShopItem> items = repository.findAll();
				JSONArray result = new JSONArray();
				for (ShopItem item : items) {
						result.put(item.toJSON());
				}
				return okJSON(result);
		}

		@GetMapping("/{id}")
		public ResponseEntity<Object> get(@PathVariable Integer id) {
				Optional<ShopItem> itemOptional = repository.findById(id);
				if (itemOptional.isPresent()) {
						return ResponseEntity.ok().body(itemOptional.get().toJSONString());
				} else {
						return notFound(id);
				}
		}

		// TODO AUTHENTICATION
		@PostMapping
		public ResponseEntity<Object> create(@RequestBody JSONObject inputJson) {
				ShopItem inputItem = new ShopItem(); inputItem.parseJSON(inputJson);
				Integer inputId = inputItem.getId();
				if (inputItem.getId() != null && repository.existsById(inputId)) {
						return ResponseEntity.badRequest().body("there already exists an item with id = " + inputId.toString());
				}
				ShopItem item = repository.save(inputItem);
				return okJSON(item.toJSON());
		}

		// TODO AUTHENTICATION
		@PutMapping("/{id}")
		public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody JSONObject itemDetails) {
				Optional<ShopItem> itemOptional = repository.findById(id);
				if (itemOptional.isPresent()) {
						ShopItem item = itemOptional.get();
						itemDetails.remove("id"); /* The route id decides the id in DB to update */
						item.parseJSON(itemDetails);
						repository.save(item);
						return okJSON(item.toJSON());
				} else { return notFound(id); }
		}

		// TODO AUTHENTICATION
		@DeleteMapping("/{id}")
		public ResponseEntity<Object> deleteById(@PathVariable Integer id) {
				Optional<ShopItem> itemOptional = repository.findById(id);
				if (itemOptional.isPresent()) {
						ShopItem item = itemOptional.get();
						repository.delete(item);
						return okJSON(item.toJSON());
				} else { return notFound(id); }
		}
}
