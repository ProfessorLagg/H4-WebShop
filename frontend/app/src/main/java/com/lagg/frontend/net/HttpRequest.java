package com.lagg.frontend.net;

import android.animation.TypeConverter;
import android.util.Log;

import com.lagg.frontend.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class HttpRequest {
		private static final String TAG = "HttpRequest";

		public String method;
		public String host;
		public int port;
		public String path;
		public Map<String, String> headers;
		private byte[] body;
		public long timeout = 1000;
		public byte[] getBody() { return body; }
		public void setBody(byte[] value) {
				this.body = value; this.headers.put("Content-Length", Integer.valueOf(this.body.length).toString());
		}

		public HttpRequest(String url) {
				this.method = "GET";
				this.headers = new HashMap<>();
				this.setBody(new byte[0]);
				this.parseUrl(url);
				this.headers.put("Host", this.host);
				this.headers.put("Connection", "keep-alive");
		}
		private void parseUrl(String url) {
				int protocolIdx = url.indexOf("://");
				if (protocolIdx >= 0) {
						url = url.substring(protocolIdx + 3);
				}

				if (url.charAt(url.length() - 1) == '/') {
						url = url.substring(0, url.length() - 1);
				}

				int pathIdx = url.indexOf('/');
				if (pathIdx > 0) {
						this.path = url.substring(pathIdx).trim();
						url = url.substring(0, pathIdx).trim();
				} else {
						this.path = "/";
						url = url.trim();
				}

				int portIdx = url.lastIndexOf(':');
				if (portIdx >= 0) {
						String portString = url.substring(portIdx + 1, url.length());
						this.port = Integer.parseInt(portString);
						url = url.substring(0, portIdx);
				} else {
						this.port = 80;
				}

				this.host = url;
		}

		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages
		private byte[] getRequestBytes() {
				byte[] nonBodyBytes = this.getRequestString().getBytes(StandardCharsets.UTF_8);
				return Utils.concatWithArrayCopy(nonBodyBytes, this.body);
		}

		private String getRequestString() {
				ArrayList<String> lines = new ArrayList<>();
				lines.add(this.method + ' ' + this.path + ' ' + "HTTP/1.1");
				for (Map.Entry<String, String> entry : this.headers.entrySet()) {
						lines.add(entry.getKey() + ": " + entry.getValue());
				}

				String result = String.join("\r\n", lines) + "\r\n";
				return result;
		}

		public String getUrl() {
				return "http://" + this.host + ':' + this.port + this.path;
		}

		public HttpResponse run() throws IOException {
				byte[] requestBytes = this.getRequestBytes();
				String requestString = new String(requestBytes, StandardCharsets.UTF_8);
				Log.i(TAG, "run: http request:\n---\n" + requestString + "\n---");


				Socket socket = new Socket(this.host, port);
				BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
				BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

				out.write(requestBytes);
				long start = System.nanoTime();
				long max_wait = timeout * 1_000_000;
				long duration = 0;
				int responseLen = in.available();
				while (duration < max_wait && responseLen <= 0) {
						responseLen = in.available();
						duration = System.nanoTime() - start;
				}

				byte[] responseBytes = new byte[responseLen];
				in.read(responseBytes);

				out.close();

				in.close();
				socket.close();

				String responseString = new String(responseBytes, StandardCharsets.UTF_8);
				Log.i(TAG, "run: http response:\n" + responseString);
				return new HttpResponse(responseBytes);
		}

		public FutureTask<HttpResponse> getTask() {
				return new FutureTask<>(() -> this.run());
		}
}
