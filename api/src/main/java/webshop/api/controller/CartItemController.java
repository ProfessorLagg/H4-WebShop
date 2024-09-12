package webshop.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webshop.api.Utils;
import webshop.api.model.CartItem;
import webshop.api.model.Category;
import webshop.api.model.Product;
import webshop.api.repository.CartItemRepository;
import webshop.api.repository.CategoryRepository;
import webshop.api.repository.ProductRepository;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartItemController {
		@Autowired
		private CartItemRepository repository;
		@Autowired
		private ProductRepository productRepo;

		@GetMapping("/{deviceId}")
		public List<CartItem> getDeviceCart(@PathVariable String deviceId) {
				return repository
								.findAll()
								.stream()
								.filter(ci -> ci.deviceId.equals(deviceId))
								.toList();
		}

		@PostMapping("/{deviceId}/add/{productId}")
		public ResponseEntity<Object> addProductToDeviceCart(@PathVariable String deviceId, @PathVariable Integer productId) {
				Optional<Product> productOptional = productRepo.findById(productId);
				if (productOptional.isEmpty()) { return Utils.notFound("Product", productId); }

				Optional<CartItem> itemOptional = repository
								.findAll()
								.stream()
								.filter(ci -> ci.deviceId.equals(deviceId) && ci.productId.equals(productId))
								.findFirst();

				CartItem inputItem;
				if (itemOptional.isPresent()) {
						inputItem = itemOptional.get();
						inputItem.count += 1;
				} else {
						inputItem = new CartItem(deviceId, productId);
				}
				CartItem outputItem = repository.save(inputItem);
				return ResponseEntity.ok(outputItem);
		}

		@DeleteMapping("/{deviceId}/remove/{productId}/{count}")
		public ResponseEntity<Object> removeProductFromDeviceCart(@PathVariable String deviceId, @PathVariable Integer productId, Integer count) {
				if (productRepo.existsById(productId)) { return Utils.notFound("Product", productId); }

				Optional<CartItem> itemOptional = repository
								.findAll()
								.stream()
								.filter(ci -> ci.deviceId.equals(deviceId) && ci.productId.equals(productId))
								.findFirst();
				if (itemOptional.isEmpty()) { return Utils.notFound("CartItem", deviceId + '/' + productId); }

				CartItem item = itemOptional.get();
				item.count = item.count - count;
				if (item.count <= 0) { repository.delete(item); }
				return ResponseEntity.ok(item);
		}

		@DeleteMapping("/{deviceId}/clear")
		public ResponseEntity<Object> clearCart(@PathVariable String deviceId) {
				List<CartItem> items = getDeviceCart(deviceId);
				for (CartItem item : items) { repository.delete(item); }
				return ResponseEntity.ok(items);
		}
}
