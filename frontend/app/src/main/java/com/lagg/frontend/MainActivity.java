package com.lagg.frontend;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lagg.frontend.model.Category;
import com.lagg.frontend.net.DataFetcher;
import com.lagg.frontend.net.HttpRequest;
import com.lagg.frontend.net.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class MainActivity extends AppCompatActivity {
		private static final String TAG = "MainActivity";
		private Executor executor = new ThreadPerTaskExecutor();
		@Override
		protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_main);
				ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
						Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
						v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
						return insets;
				});

				this.init();
		}

		private ViewGroup pageLayout;
		private void init() {
				DataFetcher.init(findViewById(R.id.main).getContext());
				this.pageLayout = (ViewGroup) findViewById(R.id.pageLayout);
				this.displayPageCategories();
		}

		private void displayPageCategories() {
				ArrayList<Category> categories = new ArrayList<>(this.getAllCategories());
				pageLayout.removeAllViews();

				for (Category category : categories) {
						Log.d(TAG, "displayPageCategories: Creating banner for category " + category.toString());
						CategoryBanner banner = new CategoryBanner(pageLayout.getContext(), category);
						pageLayout.addView(banner);
				}
		}

		private byte[] getRequest(URL url) {
				try {
						Callable<byte[]> callable = () -> {
								try {

										HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
										InputStream in = new BufferedInputStream(urlConnection.getInputStream());
										byte[] buffer = new byte[in.available()]; /* 1073741824 bytes == 1gb */
										int readsize = in.read(buffer);
										urlConnection.disconnect();
										return buffer;
								} catch (Exception e) {
										Log.d(TAG, "getAllCategories: ", e);
										return new byte[0];
								}
						};

						FutureTask<byte[]> promise = new FutureTask<>(callable);
						promise.run();
						return promise.get();
				} catch (Exception e) {
						Log.d(TAG, "getRequest: ", e);
						return new byte[0];
				}
		}

		private static List<Category> parseCategoryArray(String jsonString) {
				if (Utils.isNullOrWhitespace(jsonString)) { return Collections.emptyList(); }
				ArrayList<Category> result = new ArrayList<>();
				try {
						JSONArray jarr = new JSONArray(jsonString);
						int len = jarr.length();
						for (int i = 0; i < len; i++) {
								try {
										JSONObject jobj = jarr.getJSONObject(i);
										Category category = new Category();
										category.loadJson(jobj);
										result.add(category);
								} catch (JSONException e) {
										Log.e(TAG, "parseCategoryArray: could not parse item at index " + i + " in '" + jsonString +
														"' as a Category");
								}
						}
				} catch (Exception e) {
						Log.e(TAG, "parseCategoryArray: ", e);
						return Collections.emptyList();
				}
				return result;
		}

		/** Gets all the categories from the webserver */
		public List<Category> getAllCategories() {
				Resources resources = getResources();
				String serverHostName = resources.getString(R.string.serverHostName);
				int requestTimeout = resources.getInteger(R.integer.requestTimeout);

				String categoryHttpPath = resources.getString(R.string.categoryHttpPath);
				boolean useHttps = resources.getBoolean(R.bool.use_https);

				String url = Utils.getUrlString(serverHostName, categoryHttpPath, useHttps);
				HttpRequest request = new HttpRequest(url);
				FutureTask<HttpResponse> responseFuture = request.getTask();
				this.executor.execute(responseFuture);
				try {
						HttpResponse response = responseFuture.get();
						String jsonString = new String(response.body, StandardCharsets.UTF_8);
						return parseCategoryArray(jsonString);
				} catch (Exception e) {
						Log.e(TAG, "getAllCategories: error trying to get or parse HttpResponse", e);
						return Collections.emptyList();
				}
		}
}