package com.lagg.frontend;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lagg.frontend.model.Product;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductPage extends ConstraintLayout {
		private static final String TAG = "ProductBanner";

		private final Product product;

		public ProductPage(@NonNull Context context, Product product) {
				super(context);
				this.product = product;
				this.init();
		}

		private View contentView;
		private TextView product_page_title;
		private TextView product_page_description;
		private ImageView product_page_image;
		private View product_page_cart_btn;
		private void init() {

				contentView = inflate(getContext(), R.layout.layout_product_page, this);
				Resources resources = contentView.getResources();
				product_page_title = contentView.findViewById(R.id.product_page_title);
				product_page_description = contentView.findViewById(R.id.product_page_description);
				product_page_image = contentView.findViewById(R.id.product_page_image);
				product_page_cart_btn = contentView.findViewById(R.id.product_page_cart_btn);

				product_page_title.setText(this.product.title);

				product_page_description.setText(this.product.description);

				String subUrl = resources.getString(R.string.imageHttpPath) + '/' + this.product.image;
				String url = Utils.getUrlString(resources.getString(R.string.serverHostName), subUrl, false);
				Log.i(TAG, "init: loading image from " + url);
				Picasso.get()
								.load(url)
								.fit()
								.placeholder(R.drawable.placeholder)
								.into(this.product_page_image);

				product_page_cart_btn.setOnClickListener(v -> addToCart());
		}

		public void addToCart() {
				Context context = this.getContext();
				String deviceId = Utils.getDeviceId(context);
				String productId = this.product.id.toString();
				String productName = this.product.title;

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
												try {
														JSONObject jsonObject = new JSONObject(response);
														CharSequence text =
																		"You now have " + jsonObject.getInt("count") + "\n" + productName + " in your cart";
														int duration = Toast.LENGTH_LONG;
														Toast toast = Toast.makeText(context, text, duration);
														toast.show();
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
