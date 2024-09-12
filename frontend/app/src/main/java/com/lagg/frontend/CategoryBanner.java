package com.lagg.frontend;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lagg.frontend.model.Category;

public class CategoryBanner extends ConstraintLayout {
		public final Category category;

		public CategoryBanner(@NonNull Context context, Category category) {
				super(context);
				this.category = category;
				this.init();
		}

		private View contentView;
		private Button titleView;
		private void init() {
				contentView = inflate(getContext(), R.layout.layout_category_banner, this);
				this.titleView = (Button) contentView.findViewById(R.id.categoryTitle);
				this.titleView.setText(this.category.name);
				this.setOnClickListener((v -> titleView.callOnClick()));
		}
}
