package webshop.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;

public class Utils {

		public static ResponseEntity<Object> notFound(String objectName, Integer id) {
				return notFound(objectName, id.toString());
		}

		public static ResponseEntity<Object> notFound(String objectName, String idString) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
								.body("could not find " + objectName + " with id = " + idString);
		}

		public static ResponseEntity<Object> alreadyExists(String objectName, Integer id) {
				return alreadyExists(objectName, id.toString());
		}

		public static ResponseEntity<Object> alreadyExists(String objectName, String idString) {
				return ResponseEntity.badRequest().body("there already exists an " + objectName + " with id = " + idString);
		}

		public static byte[] reverseArray(byte[] bytes) {
				byte[] result = new byte[bytes.length];
				for (int i = 0; i < result.length; i++) {
						int j = bytes.length - 1 - i;
						result[i] = bytes[j];
				}
				return result;
		}

		private static final char[] whitespaceChars =
						new char[]{0x09 /* Horizontal Tab */, 0x0A /* Line Feed */, 0x0B /* Vertical Tabulation */, 0x0C
											 /* Form Feed */, 0x0D /* Carriage Return */, 0x20 /* Space */,};
		public static boolean isWhitespaceChar(char c) {
				for (int i = 0; i < whitespaceChars.length; i++) {
						if (c == whitespaceChars[i]) { return true; }
				} return false;
		}
		public static boolean isNullOrEmpty(String str) {
				return str == null || str.isEmpty();
		}
		public static boolean isNullOrWhitespace(String str) {
				if (isNullOrEmpty(str)) { return true; } for (char c : str.toCharArray()) {
						if (!isWhitespaceChar(c)) { return false; }
				} return true;
		}

		public static MimeType mimeTypeFromFileExtention(String extention) {
				String ext = extention.trim().toLowerCase(); switch (ext) {
						case ".aac":
								return MimeType.valueOf("audio/aac");
						case ".abw":
								return MimeType.valueOf("application/x-abiword");
						case ".apng":
								return MimeType.valueOf("image/apng");
						case ".arc":
								return MimeType.valueOf("application/x-freearc");
						case ".avif":
								return MimeType.valueOf("image/avif");
						case ".avi":
								return MimeType.valueOf("video/x-msvideo");
						case ".azw":
								return MimeType.valueOf("application/vnd.amazon.ebook");
						case ".bin":
								return MimeType.valueOf("application/octet-stream");
						case ".bmp":
								return MimeType.valueOf("image/bmp");
						case ".bz":
								return MimeType.valueOf("application/x-bzip");
						case ".bz2":
								return MimeType.valueOf("application/x-bzip2");
						case ".cda":
								return MimeType.valueOf("application/x-cdf");
						case ".csh":
								return MimeType.valueOf("application/x-csh");
						case ".css":
								return MimeType.valueOf("text/css");
						case ".csv":
								return MimeType.valueOf("text/csv");
						case ".doc":
								return MimeType.valueOf("application/msword");
						case ".docx":
								return MimeType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
						case ".eot":
								return MimeType.valueOf("application/vnd.ms-fontobject");
						case ".epub":
								return MimeType.valueOf("application/epub+zip");
						case ".gz":
								return MimeType.valueOf("application/gzip");
						case ".gif":
								return MimeType.valueOf("image/gif");
						case ".htm", " .html":
								return MimeType.valueOf("text/html");
						case ".ico":
								return MimeType.valueOf("image/vnd.microsoft.icon");
						case ".ics":
								return MimeType.valueOf("text/calendar");
						case ".jar":
								return MimeType.valueOf("application/java-archive");
						case ".jpeg", ".jpg":
								return MimeType.valueOf("image/jpeg");
						case ".js":
								return MimeType.valueOf("text/javascript (Specifications: HTML and RFC 9239)");
						case ".json":
								return MimeType.valueOf("application/json");
						case ".jsonld":
								return MimeType.valueOf("application/ld+json");
						case ".mid", ".midi":
								return MimeType.valueOf("audio/midi, audio/x-midi");
						case ".mjs":
								return MimeType.valueOf("text/javascript");
						case ".mp3":
								return MimeType.valueOf("audio/mpeg");
						case ".mp4":
								return MimeType.valueOf("video/mp4");
						case ".mpeg":
								return MimeType.valueOf("video/mpeg");
						case ".mpkg":
								return MimeType.valueOf("application/vnd.apple.installer+xml");
						case ".odp":
								return MimeType.valueOf("application/vnd.oasis.opendocument.presentation");
						case ".ods":
								return MimeType.valueOf("application/vnd.oasis.opendocument.spreadsheet");
						case ".odt":
								return MimeType.valueOf("application/vnd.oasis.opendocument.text");
						case ".oga":
								return MimeType.valueOf("audio/ogg");
						case ".ogv":
								return MimeType.valueOf("video/ogg");
						case ".ogx":
								return MimeType.valueOf("application/ogg");
						case ".opus":
								return MimeType.valueOf("audio/ogg");
						case ".otf":
								return MimeType.valueOf("font/otf");
						case ".png":
								return MimeType.valueOf("image/png");
						case ".pdf":
								return MimeType.valueOf("application/pdf");
						case ".php":
								return MimeType.valueOf("application/x-httpd-php");
						case ".ppt":
								return MimeType.valueOf("application/vnd.ms-powerpoint");
						case ".pptx":
								return MimeType.valueOf("application/vnd.openxmlformats-officedocument.presentationml.presentation");
						case ".rar":
								return MimeType.valueOf("application/vnd.rar");
						case ".rtf":
								return MimeType.valueOf("application/rtf");
						case ".sh":
								return MimeType.valueOf("application/x-sh");
						case ".svg":
								return MimeType.valueOf("image/svg+xml");
						case ".tar":
								return MimeType.valueOf("application/x-tar");
						case ".tif", ".tiff":
								return MimeType.valueOf("image/tiff");
						case ".ts":
								return MimeType.valueOf("video/mp2t");
						case ".ttf":
								return MimeType.valueOf("font/ttf");
						case ".txt":
								return MimeType.valueOf("text/plain");
						case ".vsd":
								return MimeType.valueOf("application/vnd.visio");
						case ".wav":
								return MimeType.valueOf("audio/wav");
						case ".weba":
								return MimeType.valueOf("audio/webm");
						case ".webm":
								return MimeType.valueOf("video/webm");
						case ".webp":
								return MimeType.valueOf("image/webp");
						case ".woff":
								return MimeType.valueOf("font/woff");
						case ".woff2":
								return MimeType.valueOf("font/woff2");
						case ".xhtml":
								return MimeType.valueOf("application/xhtml+xml");
						case ".xls":
								return MimeType.valueOf("application/vnd.ms-excel");
						case ".xlsx":
								return MimeType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
						case ".xml":
								return MimeType.valueOf("application/xml");
						case ".xul":
								return MimeType.valueOf("application/vnd.mozilla.xul+xml");
						case ".zip":
								return MimeType.valueOf("application/zip");
						case ".3gp":
								return MimeType.valueOf("video/3gpp");
						case ".3g2":
								return MimeType.valueOf("video/3gpp2");
						case ".7z":
								return MimeType.valueOf("application/x-7z-compressed");
						default:
								return MimeType.valueOf("text/plain");
				}
		}
		public static MimeType mimeTypeFromFileName(String filename) {
				int lastIdx = filename.lastIndexOf('.'); String extention = filename.substring(lastIdx);
				return mimeTypeFromFileExtention(extention);
		}
}
