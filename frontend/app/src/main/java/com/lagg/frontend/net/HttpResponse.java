package com.lagg.frontend.net;

import androidx.annotation.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpResponse {
		public final HttpStatusCode status;
		public final Map<String, String> headers;
		@Nullable
		public final byte[] body;

		public HttpResponse(HttpStatusCode status, Map<String, String> headers, byte[] body) {
				this.status = status;
				this.headers = new HashMap<>();
				this.headers.putAll(headers);
				this.body = new byte[body.length];
				System.arraycopy(body, 0, this.body, 0, body.length);
		}
		public HttpResponse(HttpStatusCode status, Map<String, String> headers) {
				this.status = status;
				this.headers = new HashMap<>();
				this.headers.putAll(headers);
				this.body = null;
		}

		public Optional<String> getContentType() {
				String result = null;
				if (this.headers.containsKey("Content-Type")) {
						result = this.headers.get("Content-Type");
				}
				return result == null ? Optional.empty() : Optional.of(result);
		}
		public String getBodyString() {
				Charset charset = StandardCharsets.UTF_8;
				Optional<String> contentTypeOptional = this.getContentType();
				if (contentTypeOptional.isPresent()) {
						String contentType = contentTypeOptional.get();
						if (contentType.contains("charset=")) {
								// TODO try match the charset
						}
				}
				return getBodyString(charset);
		}
		public String getBodyString(Charset charset) { return new String(this.body, charset); }
}
