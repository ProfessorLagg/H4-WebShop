package com.lagg.frontend;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lagg.frontend.model.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CartPage extends LinearLayout {
		private static final String TAG = "CartPage";

		private final Map<Integer, Integer> cartItems = new HashMap<>();
		private final List<Product> products = new ArrayList<>();
		public CartPage(@NonNull Context context) {
				super(context);
				this.init();
		}

		private View contentView;
		private ViewGroup cart_item_page_banner_list;
		private void init() {
				contentView = inflate(getContext(), R.layout.layout_cart_page, this);
				cart_item_page_banner_list = contentView.findViewById(R.id.cart_item_page_banner_list);
				Resources resources = contentView.getResources();
				displayCartItems();
		}

		private void hydrateCartItems() {
				this.cartItems.clear();
				Context context = getContext();
				Resources resources = contentView.getResources();

				String deviceId = Utils.getDeviceId(context);
				String serverHostName = resources.getString(R.string.serverHostName);
				boolean useHttps = resources.getBoolean(R.bool.use_https);
				String url = Utils.getUrlString(serverHostName, "/cart/" + deviceId, useHttps);

				RequestQueue queue = Volley.newRequestQueue(context);
				queue.start();
				queue.add(new StringRequest(
								Request.Method.GET,
								url,
								new Response.Listener<String>() {
										@Override
										public void onResponse(String response) {
												Log.i(TAG, "onResponse: Response Received hydrateCartItems");
												int len = 0;
												try {
														JSONArray jarr = new JSONArray(response);
														len = jarr.length();
														for (int i = 0; i < len; i++) {
																JSONObject jsonObject = jarr.getJSONObject(i);
																Integer productId = jsonObject.getInt("productId");
																Integer count = jsonObject.getInt("count");
																cartItems.put(productId, count);
														}
												} catch (Exception e) {
														Log.d(TAG, "onResponse: ", e);
												}

												Log.i(TAG, "onResponse: Response Done hydrateCartItems. " + len + " items found");
										}
								},
								new Response.ErrorListener() {
										@Override
										public void onErrorResponse(VolleyError error) {
												Log.e(TAG, "Error");
										}
								}));
		}

		private void incementCartItem(Integer productId) {
				Context context = this.getContext();
				String deviceId = Utils.getDeviceId(context);
				Resources resources = getResources();
				String url = Utils.getUrlString(
								resources.getString(R.string.serverHostName),
								"/cart/" + deviceId + "/add/" + productId,
								resources.getBoolean(R.bool.use_https));

				RequestQueue queue = Volley.newRequestQueue(context);
				queue.start();
				queue.add(new StringRequest(
								Request.Method.POST,
								url,
								new Response.Listener<String>() {
										@Override
										public void onResponse(String response) {
												Log.i(TAG, "onResponse: Response Received");
												Log.i(TAG, "onResponse: Response Done");
										}
								},
								new Response.ErrorListener() {
										@Override
										public void onErrorResponse(VolleyError error) {
												Log.e(TAG, "Error");
										}
								}));
				this.displayCartItems();
		}
		private void decrementCartItem(Integer productId) {
				Context context = this.getContext();
				String deviceId = Utils.getDeviceId(context);
				Resources resources = getResources();
				String url = Utils.getUrlString(
								resources.getString(R.string.serverHostName),
								"/cart/" + deviceId + "/remove/" + productId + "/1",
								resources.getBoolean(R.bool.use_https));

				RequestQueue queue = Volley.newRequestQueue(context);
				queue.start();
				queue.add(new StringRequest(
								Request.Method.DELETE,
								url,
								new Response.Listener<String>() {
										@Override
										public void onResponse(String response) {
												Log.i(TAG, "onResponse: Response Received");
												Log.i(TAG, "onResponse: Response Done");
										}
								},
								new Response.ErrorListener() {
										@Override
										public void onErrorResponse(VolleyError error) {
												Log.e(TAG, "Error");
										}
								}));
				this.displayCartItems();
		}
		private void removeCartItem(Integer productId) {
				// TODO
				this.displayCartItems();
		}
		private void createCardItemBanner(Product product, Integer count) {


		}
		private void displayCartItems() {
				hydrateCartItems();
				cart_item_page_banner_list.removeAllViews();
				Context context = getContext();
				Resources resources = contentView.getResources();
				String host = resources.getString(R.string.serverHostName);
				boolean useHttps = resources.getBoolean(R.bool.use_https);
				RequestQueue queue = Volley.newRequestQueue(context);
				queue.start();

				for (Entry<Integer, Integer> entry : cartItems.entrySet()) {
						Integer productId = entry.getKey();
						Integer count = entry.getValue();
						String subUrl = "/product/" + productId.toString();
						String url = Utils.getUrlString(host, subUrl, useHttps);
						Log.d(TAG, "displayCartItems: sending request to " + url);
						queue.add(new StringRequest(
										Request.Method.GET,
										url,
										new Response.Listener<String>() {
												@Override
												public void onResponse(String response) {
														Log.i(TAG, "onResponse: Response Received");
														try {
																JSONObject jsonObject = new JSONObject(response);
																Product product = new Product();
																product.loadJson(jsonObject);
																Context context = getContext();
																CartItemBanner banner = new CartItemBanner(context, product, count);
																banner.setIncrementCountOnClickListener(v -> { incementCartItem(product.id); });
																banner.setDecrementCountOnClickListener(v -> { decrementCartItem(product.id); });
																banner.setRemoveItemClickListener(v -> { removeCartItem(product.id); });
																cart_item_page_banner_list.addView(banner);
																Log.i(TAG, "created cart item banner for: " + product.toString());
														} catch (Exception e) {
																Log.e(TAG, "onResponse: ", e);
														}

														Log.i(TAG, "onResponse: Response Done");
												}
										},
										new Response.ErrorListener() {
												@Override
												public void onErrorResponse(VolleyError error) {
														Log.e(TAG, "Error");
												}
										}));
				}
		}
}
