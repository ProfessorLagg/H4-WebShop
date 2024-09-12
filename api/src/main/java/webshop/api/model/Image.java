package webshop.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Image {
		@Id
		@Column(name = "name", columnDefinition = "VARCHAR(MAX)")
		public String name;

		@Column(name = "data", columnDefinition = "VARBINARY(MAX)")
		public byte[] data;

		public void cloneFrom(Image other, boolean cloneId) {
				if (cloneId && other.name != null) { this.name = other.name; }
				if (other.data != null) { this.data = other.data; }
		}
}
