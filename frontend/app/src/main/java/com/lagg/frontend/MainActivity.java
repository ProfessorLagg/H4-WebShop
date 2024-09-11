package com.lagg.frontend;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lagg.frontend.model.Category;
import com.lagg.frontend.net.DataFetcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";

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

	private DataFetcher dataFetcher;

	private void init() {
		dataFetcher = new DataFetcher(getBaseContext());
		Optional<Category> optionalCategory = this.dataFetcher.getCategory(1);
		String categoryString = "null";
		if (optionalCategory.isPresent()) {
			try {
				categoryString = optionalCategory.get().toJson().toString();
			} catch (Exception ignored) {
			}

		}
		Log.d(TAG, "init: fetched category: " + categoryString);
	}
}