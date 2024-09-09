package webshop.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalUtils {

		public static ResponseEntity<Object> notFound(String objectName, Integer id) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
								.body("could not find " + objectName + " with id = " + id.toString());
		}

		public static  ResponseEntity<Object> alreadyExists(String objectName, Integer id) {
				return ResponseEntity
								.badRequest()
								.body("there already exists an " + objectName + " with id = " + id.toString());
		}
}
