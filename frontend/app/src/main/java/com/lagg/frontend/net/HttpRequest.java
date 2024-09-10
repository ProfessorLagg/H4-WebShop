package com.lagg.frontend.net;

import com.lagg.frontend.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
		public HttpMethod method;
		private String host;
		private int port = -1;
		public int getPort() { return this.port; }
		public void setPort(int value) { this.port = Utils.clamp(value, 0, 65535); }
		public String getHost() { return this.host; }
		public void setHost(String value) { this.host = Utils.trim(value.trim(), '/'); }
		private String path;
		public String getPath() { return this.path; }
		public void setPath(String value) { this.path = Utils.trim(value.trim(), '/'); }
		public final Map<String, String> parameters = new HashMap<>();
		public final Map<String, String> headers = new HashMap<>();
		public byte[] body;

		/** Set the body bytes to UTF-8 encoded string and the content type to "text/plain;charset=UTF-8" */
		public void setStringBody(String str) {
				this.body = str.getBytes(StandardCharsets.UTF_8);
				this.headers.put("Content-Type", "text/plain;charset=UTF-8");
		}
		/** Set the body bytes to UTF-8 encoded string and the content type to "application/json;charset=UTF-8" */
		public void setJsonBody(JSONObject json) {
				String str = json.toString();
				this.body = str.getBytes(StandardCharsets.UTF_8);
				this.headers.put("Content-Type", "application/json;charset=UTF-8");
		}
		/** Set the body bytes to UTF-8 encoded string and the content type to "application/json;charset=UTF-8" */
		public void setJsonBody(JSONArray json) {
				String str = json.toString();
				this.body = str.getBytes(StandardCharsets.UTF_8);
				this.headers.put("Content-Type", "application/json;charset=UTF-8");
		}

		private enum HttpProtocol {
				HTTP("http", 80),
				HTTPS("https", 443);

				public final String urlvalue;
				public final int port;
				HttpProtocol(String v, int p) {
						this.urlvalue = v;
						this.port = p;
				}
		}
		private URL getUrl(HttpProtocol protocol) throws MalformedURLException {
				int urlport = this.port <= 0 ? protocol.port : this.port;
				// URL(String protocol, String host, int port, String file, URLStreamHandler handler
				return new URL(protocol.urlvalue, this.host, port, this.path);
		}
		private String getParamsString() throws UnsupportedEncodingException {
				StringBuilder sb = new StringBuilder();
				char prependChar = '?';
				for (String k : this.parameters.keySet()) {
						String v = this.parameters.get(k);
						sb.append(prependChar);
						sb.append(k);
						sb.append('=');
						sb.append(v);
						if (prependChar == '?') { prependChar = '&'; }
				}
				String paramsString = sb.toString();
				String result = URLEncoder.encode(paramsString, "UTF-8");
				return result;
		}
		public String getURLString() {
				try {
						URL url = this.getUrl(HttpProtocol.HTTP);
						String urlstring = url.toString();
						return url + this.getParamsString();
				} catch (Exception e) {
						return "";
				}
		}
		public HttpResponse sendHttp() throws MalformedURLException, IOException { return sendHttp(1000); }
		public HttpResponse sendHttp(int timeOut) throws MalformedURLException, IOException {
				URL url = this.getUrl(HttpProtocol.HTTP);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod(this.method.toString());
				con.setConnectTimeout(timeOut);
				con.setInstanceFollowRedirects(true);
				// Headers
				for (String k : this.headers.keySet()) {
						String v = this.headers.get(k);
						con.setRequestProperty(k, v);
				}

				con.setDoOutput(true);
				DataOutputStream out = new DataOutputStream(con.getOutputStream());
				out.writeBytes(this.getParamsString());
				out.flush();
				out.close();

				int status = con.getResponseCode();
				int inputSize;
				int readSize;
				byte[] responseBytes;

				InputStream inputStream = con.getInputStream();
				inputSize = inputStream.available();
				responseBytes = new byte[inputSize];
				readSize = inputStream.read(responseBytes);
				inputStream.close();

				HttpResponse response = new HttpResponse(HttpStatusCode.OK, new HashMap<>());

				return response;
		}
}
