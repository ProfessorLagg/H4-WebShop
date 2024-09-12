package com.lagg.frontend;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lagg.frontend.model.Product;
import com.squareup.picasso.Picasso;

public class ProductBanner extends ConstraintLayout {
		private static final String TAG = "ProductBanner";

		private final Product product;

		public ProductBanner(@NonNull Context context, Product product) {
				super(context);
				this.product = product;
				this.init();
		}

		private View contentView;
		private ImageView productImage;
		private TextView productName;
		private void init() {
				contentView = inflate(getContext(), R.layout.layout_product_banner, this);

				this.productImage = (ImageView) contentView.findViewById(R.id.productImage);
				Resources resources = getResources();
				String subUrl = resources.getString(R.string.imageHttpPath) + '/' + this.product.image;
				String url = Utils.getUrlString(resources.getString(R.string.serverHostName), subUrl, false);
				int imgsize = Math.min(contentView.getWidth(), contentView.getHeight()) / 4;
				Log.i(TAG, "init: loading image from " + url);
				Picasso.get()
								.load(url)
								.fit()
								.placeholder(R.drawable.placeholder)
								.into(this.productImage);

				this.productName = (TextView) contentView.findViewById(R.id.productName);
				productName.setText(this.product.title);

				productImage.setOnClickListener(v -> this.callOnClick());
				productName.setOnClickListener(v -> this.callOnClick());
		}
}
