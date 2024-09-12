package webshop.api.model;

import jakarta.persistence.*;
import webshop.api.Utils;

@Entity
public class CartItem {
		@Column(name = "name")
		public String deviceId;

		public Integer productId;

		public Integer count;

		public CartItem(String deviceId, Integer productId){
				this.deviceId = deviceId;
				this.productId = productId;
				this.count = 1;
		}

		public void cloneFrom(CartItem other, boolean cloneId) {
				if (!Utils.isNullOrWhitespace(other.deviceId)) { this.deviceId = other.deviceId; }
				if (other.productId != null && other.productId > 0) { this.productId = other.productId; }
		}
}
