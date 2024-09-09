package webshop.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webshop.api.model.*;
import webshop.api.DTO.*;
import webshop.api.repository.*;

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
		private ResponseEntity<Object> alreadyExists(Integer id) {
				return ResponseEntity
								.badRequest()
								.body("there already exists an item with id = " + id.toString());
		}

		@GetMapping
		public ResponseEntity<Object> getAll() {
				List<ShopItem> items = repository.findAll();
				return ResponseEntity.ok(items);
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
		public ResponseEntity<Object> create(@RequestBody ShopItemDTO inDTO) {
				if (inDTO.id != null && repository.existsById(inDTO.id)) { return alreadyExists(inDTO.id); }
				ShopItem inItem = inDTO.toReal();
				ShopItem outItem = repository.save(inItem);
				ShopItemDTO outDTO = new ShopItemDTO(outItem);
				return ResponseEntity.ok(outDTO);
		}

		// TODO AUTHENTICATION
		@PutMapping("/{id}")
		public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody ShopItem inItem) {
				Optional<ShopItem> itemOptional = repository.findById(id);
				if (itemOptional.isPresent()) {
						ShopItem item = itemOptional.get();
						item.cloneFrom(inItem, false);
						ShopItem outItem = repository.save(item);
						return ResponseEntity.ok(outItem);
				} else { return notFound(id); }
		}

		// TODO AUTHENTICATION
		@DeleteMapping("/{id}")
		public ResponseEntity<Object> deleteById(@PathVariable Integer id) {
				Optional<ShopItem> itemOptional = repository.findById(id);
				if (itemOptional.isPresent()) {
						ShopItem item = itemOptional.get();
						repository.delete(item);
						return ResponseEntity.ok(item);
				} else { return notFound(id); }
		}
}
