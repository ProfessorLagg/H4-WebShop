package webshop.api.model;

import jakarta.persistence.*;

@Entity
public class Category {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "id")
		public Integer id;

		@Column(name = "name")
		public String name;

		public void cloneFrom(Category other, boolean cloneId) {
				if (cloneId && other.id != null) { this.id = other.id; }
				if (other.name != null) { this.name = other.name; }
		}
}
