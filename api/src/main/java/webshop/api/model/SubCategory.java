package webshop.api.model;

import jakarta.persistence.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import webshop.api.interfaces.JSONSerializeable;
import webshop.api.repository.*;

import java.util.List;

@Entity
@Table(name = "SubCategory")
public class SubCategory implements JSONSerializeable {
		@Id
		private Integer id;
		public Integer getId() { return this.id; }

		@Column(name = "Name")
		public String name;

		@Column(name = "ParentId")
		private int parentId;
		public int getParentId() { return parentId; }

		@Override
		public JSONObject toJSON() {
				JSONObject result = new JSONObject();
				result.put("id", this.id);
				result.put("name", this.name);
				result.put("parentId", this.parentId);
				return result;
		}

		@Override
		public void parseJSON(JSONObject json) {
				if (json.has("id")) { this.id = json.getInt("id"); }
				if (json.has("name")) { this.name = json.getString("name"); }
				if (json.has("parentId")) { this.parentId = json.getInt("parentId"); }
		}
		@Override
		public String toJSONString() { return this.toJSON().toString(); }
}
