package webshop.api.interfaces;

import org.json.*;

public interface JSONSerializeable {
		public JSONObject toJSON();

		public void parseJSON(JSONObject json) throws JSONException;

		public String toJSONString();
}
