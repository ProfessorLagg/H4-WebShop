package com.lagg.frontend;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lagg.frontend.model.Category;
import com.lagg.frontend.net.DataFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
				ArrayList<Category> categories = new ArrayList<>(DataFetcher.getAllCategories());
				pageLayout.removeAllViews();

				for (Category category : categories) {
						Log.d(TAG, "displayPageCategories: Creating banner for category " + category.toString());
						CategoryBanner banner = new CategoryBanner(pageLayout.getContext(), category);
						pageLayout.addView(banner);
				}
		}
}