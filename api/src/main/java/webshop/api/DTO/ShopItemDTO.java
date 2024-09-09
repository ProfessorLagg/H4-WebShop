package webshop.api.DTO;

import jakarta.annotation.Nullable;
import webshop.api.model.ShopItem;

import java.math.BigDecimal;

public class ShopItemDTO implements DataTransferObject<ShopItem> {
		@Nullable
		public Integer id;
		@Nullable
		public String name;
		@Nullable
		public String description;
		@Nullable
		public BigDecimal price;
		@Nullable
		public BigDecimal salePrice;
		@Nullable
		public String currencyCode;
		@Nullable
		public Integer categoryId;
		@Nullable
		public Integer subCategoryId;

		public ShopItemDTO() {
				Integer id = null;
				String name = null;
				String description = null;
				BigDecimal price = null;
				BigDecimal salePrice = null;
				String currencyCode = null;
				Integer categoryId = null;
				Integer subcategoryId = null;
		}
		public ShopItemDTO(ShopItem shopItem) { this.fromReal(shopItem); }

		@Override
		public ShopItem toReal() { return new ShopItem(this); }

		@Override
		public void fromReal(ShopItem shopItem) {
				this.id = shopItem.getId();
				this.name = shopItem.name;
				this.description = shopItem.description;
				this.price = shopItem.price;
				this.salePrice = shopItem.salePrice;
				this.currencyCode = shopItem.currency.getCurrencyCode();
				this.categoryId = shopItem.getCategoryId();
				this.subCategoryId = shopItem.getSubCategoryId();
		}
}
