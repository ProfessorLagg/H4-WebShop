package com.lagg.frontend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ArrayListMap<TKey, TVal> implements Map<TKey, TVal> {
		private ArrayList<TKey> keys;
		private ArrayList<TVal> values;

		private class LocalEntry implements Entry<TKey, TVal> {
				private final TKey key;
				private TVal val;

				private LocalEntry(TKey k, TVal v) {
						this.key = k;
						this.val = v;
				}

				@Override
				public TKey getKey() { return key; }
				@Override
				public TVal getValue() { return val; }
				@Override
				public TVal setValue(TVal value) { this.val = value; return this.val; }
		}

		@Override
		public int size() {
				return keys.size();
		}

		@Override
		public boolean isEmpty() {
				return keys.isEmpty();
		}

		@Override
		public boolean containsKey(@Nullable Object key) {
				return keys.contains((TKey) key);
		}

		@Override
		public boolean containsValue(@Nullable Object value) {
				return values.contains((TVal) value);
		}

		@Nullable
		@Override
		public TVal get(@Nullable Object key) {
				if (key == null) {
						return null
				}
				int index = keys.indexOf(key);
				if (index < 0 || index > keys.size()) {
						return null;
				}
				return values.get(index);
		}

		@Nullable
		@Override
		public TVal put(TKey key, TVal value) {
				int index = keys.indexOf(key);
				if (index >= 0) {
						values.set(index, value);
						return values.get(index);
				} else {
						keys.add(key);
						return this.put(key, value);
				}
		}

		@Nullable
		@Override
		public TVal remove(@Nullable Object key) {
				int index = this.keys.indexOf(key);
				if (index < 0) {
						return null;
				}

				TVal value = values.get(index);
				this.keys.remove(index);
				this.values.remove(index);
				return value;
		}

		@Override
		public void putAll(@NonNull Map<? extends TKey, ? extends TVal> m) {
				for (Entry<TKey, TVal> entry : m) {
						this.put(entry.getKey(), entry.getValue());
				}
		}

		@Override
		public void clear() {
				this.keys.clear();
				this.values.clear();
		}

		@NonNull
		@Override
		public Set<TKey> keySet() {
				return this.keys.stream().collect(Collectors.toSet());
		}

		@NonNull
		@Override
		public Collection<TVal> values() {
				return this.values.stream().collect(Collectors.toSet());
		}

		@NonNull
		@Override
		public Set<Entry<TKey, TVal>> entrySet() {
				ArrayList<Entry<TKey, TVal>> entries = new ArrayList<>();
				for (int i = 0; i < this.keys.size(); i++) {
						entries.add(new LocalEntry(keys.get(i), values.get(i)));
				}
				return entries.stream().collect(Collectors.toSet());
		}
}
