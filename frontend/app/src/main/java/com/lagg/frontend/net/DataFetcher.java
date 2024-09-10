package com.lagg.frontend.net;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.lagg.frontend.CallbackRunnable;
import com.lagg.frontend.R;
import com.lagg.frontend.ThreadPerTaskExecutor;
import com.lagg.frontend.model.Category;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

// https://www.baeldung.com/java-http-request
public class DataFetcher {
		// STATIC VALUES
		private static final Executor executor = new ThreadPerTaskExecutor();
		private final String[] serverHostNames;
		private final boolean useHttps;
		private final int requestTimeout;
		private final String categoryHttpPath;
		private final int httpPort;
		// CONSTRUCTORS
		public DataFetcher(Context context) {
				Resources resources = context.getResources();
				this.serverHostNames = resources.getStringArray(R.array.serverHostNames);
				this.useHttps = resources.getBoolean(R.bool.use_https);
				this.requestTimeout = resources.getInteger(R.integer.requestTimeout);
				this.categoryHttpPath = resources.getString(R.string.categoryHttpPath);
				this.httpPort = resources.getInteger(R.integer.httpPort);
				executor.execute(this::loadCategories);
		}

		// CATEGORY
		private final Map<Integer, Category> categoryCache = new HashMap<>();
		private void loadCategories() {
				categoryCache.clear();
				for (String host : serverHostNames) {
						HttpRequest request = new HttpRequest();
						request.method = HttpMethod.GET;
						request.setHost(host);
						request.setPort(this.httpPort);
						request.setPath(categoryHttpPath);
						try {
								HttpResponse response = request.sendHttp(requestTimeout);
								Log.d("DataFecther", "Succesfully fetched categories from: " + request.getURLString());
						} catch (MalformedURLException e) {
								String urlstring = (this.useHttps ? "https://" : "http://") + host + ':' + this.httpPort;
								Log.e("DataFetcher", "loadCategories: error building url: " + urlstring, e);
						} catch (IOException e) {
								Log.e("DataFetcher", "loadCategories: error sending request: " + request.getURLString(), e);
						}
				}
		}

		public Optional<Category> getCategory(Integer id) {
				Category result = categoryCache.get(id);
				if (result == null) { return Optional.empty(); }
				return Optional.of(result);
		}
}
