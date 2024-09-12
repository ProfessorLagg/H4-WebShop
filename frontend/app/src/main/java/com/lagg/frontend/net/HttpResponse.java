package com.lagg.frontend.net;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lagg.frontend.Utils;

public class HttpResponse {
		private static final String TAG = "HttpResponse";

		@NotNull
		public int status;
		public final Map<String, String> headers = new HashMap<>();
		public byte[] body;

		public HttpResponse(byte[] responseBytes) {
				String responseString = new String(responseBytes, StandardCharsets.UTF_8);

				int statusLineIdx = responseString.indexOf('\n');
				String statusLine = responseString.substring(0, statusLineIdx).trim();

				responseString = responseString.substring(statusLineIdx).trim();


				int bodyStartIndex = responseString.indexOf("\r\n\r\n");
				String headerString = responseString.substring(0, bodyStartIndex).trim();
				String bodyString = responseString.substring(bodyStartIndex).trim();


				this.body = bodyString.getBytes(StandardCharsets.UTF_8);

				this.status = -1;
				try {
						String statusString = statusLine.substring(statusLine.indexOf(' '));
						this.status = Integer.parseInt(statusString.substring(0, statusString.indexOf(' ')));
				} catch (Exception e) {
						Log.e(TAG, "HttpResponse: could not parse status code", e);
				}

				String[] headerLines = headerString.split("\n");
				for (String line : headerLines) {
						int i = line.indexOf(':');
						String k = line.substring(0, i - 1);
						String v = line.substring(i + 1, line.length());
						this.headers.put(k, v);
				}
		}
}
