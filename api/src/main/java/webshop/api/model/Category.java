package webshop.api.model;

import jakarta.persistence.*;
import org.json.JSONObject;
import webshop.api.interfaces.JSONSerializeable;

@Entity
@Table(name = "Category")
public class Category implements JSONSerializeable {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "Id")
		private Integer id;
		public Integer getId() { return this.id; }

		@Column(name = "Name")
		public String name;
		@Override
		public JSONObject toJSON() {
				JSONObject result = new JSONObject();
				result.put("id", this.id);
				result.put("name", this.name);
				return result;
		}
		@Override
		public void parseJSON(JSONObject json) {
				if (json.has("id")) { this.id = json.getInt("id"); }
				if (json.has("name")) { this.name = json.getString("name"); }
		}
		@Override
		public String toJSONString() { return this.toJSON().toString(); }

		public Category() {
				this.id = -1;
				this.name = "";
		}
}
