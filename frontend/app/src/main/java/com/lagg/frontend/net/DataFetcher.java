package com.lagg.frontend.net;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.Nullable;

import com.lagg.frontend.CallbackRunnable;
import com.lagg.frontend.R;
import com.lagg.frontend.ThreadPerTaskExecutor;
import com.lagg.frontend.Utils;
import com.lagg.frontend.model.Category;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.*;

// https://www.baeldung.com/java-http-request
public class DataFetcher {
		// STATIC VALUES
		private static final Executor executor = Executors.newFixedThreadPool(2);
		private static final String TAG = "DataFetcher";
		private static void LogDebug(String method, UrlRequest request, UrlResponseInfo info) {
				String msg = method + ':';
				if (info != null) {
						msg += "\n\tinfo: " + info.getUrl() + ' ' + info.getHttpStatusText() + "\n";
				}
				if (request != null) {
						msg += "\n\trequest: " + request;
				}
				Log.d(TAG, msg);
		}
		private static void LogError(String method, UrlResponseInfo info, Exception e) {
				String msg = method + ':';
				if (info != null) {
						msg += "\n\tinfo: " + info.getUrl() + ' ' + info.getHttpStatusText() + "\n";
				}
				Log.e(TAG, msg, e);
		}
		//  VALUES
		private final CronetEngine cronetEngine;
		private final String[] serverHostNames;
		private final boolean useHttps;
		private final long requestTimeout;

		private final String categoryHttpPath;

		// CONSTRUCTORS
		public DataFetcher(Context context) {
				Resources resources = context.getResources();
				this.serverHostNames = resources.getStringArray(R.array.serverHostNames);
				this.useHttps = resources.getBoolean(R.bool.use_https);
				this.requestTimeout = resources.getInteger(R.integer.requestTimeout);
				this.categoryHttpPath = resources.getString(R.string.categoryHttpPath);


				CronetEngine.Builder engineBuilder = new CronetEngine.Builder(context);
				this.cronetEngine = engineBuilder.build();
				loadCategories();
		}

		// NETWORKING
		private UrlRequest buildRequest(String method, String host, String path, UrlRequest.Callback callback) {
				return this.buildRequest(method, host, path, null, null, null, callback);
		}
		private UrlRequest buildRequest(
						String method,
						String host,
						String path,
						@Nullable Map<String, String> urlParameters,
						@Nullable Map<String, String> headers,
						@Nullable byte[] body,
						UrlRequest.Callback callback
		) {
				String urlstring = Utils.getUrlString(host, path, useHttps, urlParameters);
				UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(urlstring, callback, executor);
				requestBuilder.setHttpMethod(method);
				if (headers != null && !headers.isEmpty()) {
						String v = "";
						for (String k : headers.keySet()) {
								v = headers.get(k);
								requestBuilder.addHeader(k, v);
						}
				}

				return requestBuilder.build();
		}
		// CATEGORY
		private final Map<Integer, Category> categoryCache = new HashMap<>();
		private void handleCategoriesResponse(UrlRequest request, UrlResponseInfo info) {
				LogDebug("handleCategoriesResponse", request, info);
		}
		private void loadCategories() {
				categoryCache.clear();
				GenericRequestCallback[] callbacks = new GenericRequestCallback[serverHostNames.length];
				for (int i = 0; i < serverHostNames.length; i++) {
						String host = serverHostNames[i];
						callbacks[i] = new GenericRequestCallback(this::handleCategoriesResponse);
						UrlRequest request = buildRequest("GET", host, categoryHttpPath, callbacks[i]);
						Log.d(TAG, "loadCategories: GET " + Utils.getUrlString(host, categoryHttpPath, useHttps) + '(' +
										request.toString() + ')');
						request.start();
				}

				boolean waitForLoad;
				do {
						waitForLoad = false;
						for (GenericRequestCallback callback : callbacks) {
								boolean finished = callback.finished();

								if (!finished) { waitForLoad = true; }
						}
				} while (waitForLoad);
		}

		public Optional<Category> getCategory(Integer id) {
				Log.d(TAG, "getCategory: " + id);
				Category result = categoryCache.get(id);
				if (result == null) { return Optional.empty(); }
				return Optional.of(result);
		}
}
