package com.lagg.frontend;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lagg.frontend.model.Product;

public class CartItemBanner extends ConstraintLayout {
		private static final String TAG = "CartItemBanner";

		private final Product product;
		private Integer count;
		public CartItemBanner(@NonNull Context context, Product product, int count) {
				super(context);
				this.product = product;
				this.count = count;
				this.init();
		}

		private View contentView;
		private View cart_item_banner_controls_wrap;
		private TextView cart_item_banner_count;
		private View cart_item_banner_remove_item_btn;
		private View cart_item_banner_increment_count;
		private View cart_item_banner_decrement_count;
		private TextView cart_item_banner_title;
		private void init() {
				contentView = inflate(getContext(), R.layout.layout_cart_item_banner, this);
				this.cart_item_banner_controls_wrap = contentView.findViewById(R.id.cart_item_banner_controls_wrap);
				this.cart_item_banner_count = contentView.findViewById(R.id.cart_item_banner_count);
				this.cart_item_banner_remove_item_btn = contentView.findViewById(R.id.cart_item_banner_remove_item_btn);
				this.cart_item_banner_increment_count = contentView.findViewById(R.id.cart_item_banner_increment_count);
				this.cart_item_banner_decrement_count = contentView.findViewById(R.id.cart_item_banner_decrement_count);
				this.cart_item_banner_title = contentView.findViewById(R.id.cart_item_banner_title);

				cart_item_banner_title.setText(this.product.title); cart_item_banner_count.setText(this.count.toString());
		}

		public void setRemoveItemClickListener(OnClickListener listener) {
				this.cart_item_banner_remove_item_btn.setOnClickListener(listener);
		}

		public void setIncrementCountOnClickListener(OnClickListener listener) {
				this.cart_item_banner_increment_count.setOnClickListener(listener);
		}

		public void setDecrementCountOnClickListener(OnClickListener listener) {
				this.cart_item_banner_decrement_count.setOnClickListener(listener);
		}
}
