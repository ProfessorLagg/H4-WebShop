package com.lagg.frontend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lagg.frontend.model.Category;

public class CategoryBanner extends ConstraintLayout {
		private final Category category;

		public CategoryBanner(@NonNull Context context, Category category) {
				super(context);
				this.category = category;
				this.init();
		}

		private View contentView;
		private TextView titleView;
		private void init() {
				contentView = inflate(getContext(), R.layout.layout_category_banner, this);
				this.titleView = (TextView) contentView.findViewById(R.id.categoryTitle);
				this.titleView.setText(this.category.name);
				this.layout(0, 0, 0, 0);
		}
}
