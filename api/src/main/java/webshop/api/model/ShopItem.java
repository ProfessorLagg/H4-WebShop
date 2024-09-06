package webshop.api.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.json.JSONObject;
import webshop.api.interfaces.JSONSerializeable;
import webshop.api.repository.CategoryRepository;
import webshop.api.repository.SubCategoryRepository;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

@Entity
@Table(name = "ShopItem")
public class ShopItem implements JSONSerializeable {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Integer id;
		public Integer getId() { return this.id; }

		@Column(name = "Name")
		public String name;

		@Column(name = "Description")
		public String description;

		@Column(name = "Price")
		public BigDecimal price;

		@Column(name = "SalePrice", nullable = true)
		@Nullable
		public BigDecimal salePrice;

		@Column(name = "Currency")
		public Currency currency;

		@Column(name = "CategoryId")
		/** database id of the category*/
		private int categoryId;
		/** returns the database id of this items category */
		public int getCategoryId() { return this.categoryId; }
		public Optional<Category> getCategory(CategoryRepository repository) {
				return repository.findById(this.categoryId);
		}
		public void setCategory(Category value) {
				// TODO Check that the category id actually exists
				// TODO Figure out if i can keep the sub category or not
				this.categoryId = value.getId();
		}

		/** database id of the sub category. -1 means this shop item does not have a sub category */
		@Column(name = "SubCategoryId")
		private int subCategoryId;
		/** returns the database id of the sub category, or -1 if this shop item doesn't have a sub category */
		public int getSubCategoryId() { return this.subCategoryId; }

		public Optional<SubCategory> getSubCategory(SubCategoryRepository repository) {
				return repository.findById(this.subCategoryId);
		}
		/** Sets both the subcategory and the category */
		public void setSubCategory(SubCategory value) {
				// TODO Check that the sub category id actually exists
				this.subCategoryId = value.getId();
				this.categoryId = value.getParentId();
		}

		@Override
		public JSONObject toJSON() {
				JSONObject result = new JSONObject();
				result.put("id", this.id);
				result.put("name", this.name);
				result.put("description", this.description);
				result.put("price", this.price);
				if (this.salePrice != null) { result.put("salePrice", this.salePrice); }
				result.put("currency", this.currency.getCurrencyCode());
				result.put("categoryId", this.categoryId);
				if (this.subCategoryId >= 0) { result.put("subcategoryId", this.subCategoryId); }

				return result;
		}

		@Override
		public void parseJSON(JSONObject json) {
				if (json.has("id")) { this.id = json.getInt("id"); }
				if (json.has("name")) { this.name = json.getString("name"); }
				if (json.has("description")) { this.description = json.getString("description"); }
				if (json.has("price")) { this.price = json.getBigDecimal("price"); }
				if (json.has("salePrice")) { this.salePrice = json.getBigDecimal("salePrice"); }
				if (json.has("currency")) { this.currency = Currency.getInstance(json.getString("currency")); }
				if (json.has("category")) { this.categoryId = json.getInt("category"); }
				if (json.has("subcategoryId")) {
						this.subCategoryId = json.getInt("subcategoryId");
				} else {
						this.subCategoryId = -1;
				}
		}

		@Override
		public String toJSONString() { return this.toJSON().toString(); }

		public void cloneFrom(ShopItem item) { this.cloneFrom(item, false); }
		public void cloneFrom(ShopItem item, boolean cloneId) {
				if (cloneId) {
						this.id = item.id;
				}
				this.name = item.name;
				this.description = item.description;
				this.price = item.price;
				this.salePrice = item.salePrice;
				this.categoryId = item.categoryId;
				this.subCategoryId = item.subCategoryId;
		}

		public static final ShopItem DEFAULT_ITEM = getDefaultItem();
		private static ShopItem getDefaultItem() {
				ShopItem result = new ShopItem();
				result.id = 0;
				result.name = "name";
				result.description = "description";
				result.currency = Currency.getInstance("DKK");
				result.price = BigDecimal.valueOf(99.99);
				result.salePrice = result.price.multiply(BigDecimal.valueOf(0.8));
				return result;
		}
		public static ShopItem getDefaultItem(SubCategory subCategory) {
				ShopItem result = getDefaultItem();
				result.setSubCategory(subCategory);
				return result;
		}
}
