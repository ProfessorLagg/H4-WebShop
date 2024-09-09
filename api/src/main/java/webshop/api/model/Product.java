package webshop.api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Product")
public class Product {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "id")
		public Integer id;

		@Column(name = "title")
		public String title;

		@Column(name = "price")
		public BigDecimal price;

		@Column(name = "description", columnDefinition="varchar(MAX)")
		public String description;

		// TODO SAVE THE IMAGES IN THE DATABASE AND MAKE A CONTROLLER
		@Column(name = "image")
		public String image;

		@ManyToOne
		@JoinColumn(name = "categoryId")
		public Category category;

		public void cloneFrom(Product other, boolean cloneId) {
				if (cloneId && other.id != null) { this.id = other.id; }
				if (other.title != null) { this.title = other.title; }
				if (other.price != null) { this.price = other.price; }
				if (other.description != null) { this.description = other.description; }
				if (other.image != null) { this.image = other.image; }
				if (other.category != null) { this.category = other.category; }
		}
}
