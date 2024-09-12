package com.lagg.frontend;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import kotlin.text.UStringsKt;

public class Utils {
		public static String trimStart(String s, char c) {
				String S = s + "";
				while (S.charAt(0) == c) {
						S = S.substring(1);
				}
				return S;
		}

		public static String trimEnd(String s, char c) {
				String S = s + "";
				while (S.charAt(s.length() - 1) == c) {
						S = S.substring(0, s.length() - 1);
				}
				return S;
		}

		public static String trim(String s, char c) {
				String S = s + "";
				while (S.charAt(s.length() - 1) == c) {
						S = S.substring(0, s.length() - 1);
				}
				while (S.charAt(0) == c) {
						S = S.substring(1);
				}
				return S;
		}

		public static int clamp(int value, int min, int max) {
				return Math.max(min, Math.min(value, max));
		}

		private static class SyncRunnable implements Runnable {
				private boolean started;

				public boolean isStarted() {
						return this.started;
				}

				private boolean finished;

				public boolean isFinished() {
						return this.finished;
				}

				public final Runnable runnable;

				public SyncRunnable(Runnable runnable) {
						this.started = false;
						this.finished = false;
						this.runnable = runnable;
				}

				@Override
				public void run() {
						this.started = true;
						this.finished = false;
						runnable.run();
						this.finished = true;
				}
		}

		public static void executeSync(Executor executor, Runnable runnable) {
				SyncRunnable syncRunnable = new SyncRunnable(runnable);
				executor.execute(syncRunnable);
				while (!syncRunnable.isStarted() && !syncRunnable.isFinished()) {
				}
		}

		public static String getUrlString(String host, String path, boolean useHttps) {
				return getUrlString(host, path, useHttps, null);
		}

		public static String getUrlString(String host, String path, boolean useHttps, Map<String, String> urlParams) {
				String h = Utils.trim(host.trim(), '/');
				String p = Utils.trim(path.trim(), '/');
				String url = (useHttps ? "https://" : "http://") + h + '/' + p;

				if (urlParams != null && !urlParams.isEmpty()) {
						url += getParamsString(urlParams, false);
				}

				return url;
		}

		public static String getParamsString(Map<String, String> parameters) {
				return getParamsString(parameters, false);
		}

		public static String getParamsString(Map<String, String> parameters, boolean urlEncode) {
				StringBuilder sb = new StringBuilder();
				char prependChar = '?';
				for (String k : parameters.keySet()) {
						String v = parameters.get(k);
						sb.append(prependChar);
						sb.append(k);
						sb.append('=');
						sb.append(v);
						if (prependChar == '?') {
								prependChar = '&';
						}
				}
				String paramsString = sb.toString();
				String result = paramsString;
				if (urlEncode) {
						// SAFE TO IGNORE UnsupportedEncodingException
						try {
								result = URLEncoder.encode(paramsString, "UTF-8");
						} catch (UnsupportedEncodingException e) {
						}
				}

				return result;
		}

		public static byte[] concatWithArrayCopy(byte[] array1, byte[] array2) {
				byte[] result = Arrays.copyOf(array1, array1.length + array2.length);
				System.arraycopy(array2, 0, result, array1.length, array2.length);
				return result;
		}

		public static byte[] slice(byte[] arr, int start, int end) {
				int len = end - start;
				byte[] result = new byte[len];
				System.arraycopy(arr, start, result, 0, len);
				return result;
		}

		public static List<String> split(String str, String sep) {
				ArrayList<String> result = new ArrayList<>();
				int sepidx = str.indexOf(sep);
				while (sepidx > 0) {
						String sub = str.substring(0, sepidx);
						result.add(sub);
						str = str.substring(sub.length());
						sepidx = str.indexOf(sep);
				}
				if (!str.isEmpty()) { result.add(str); }
				return result;
		}
		private static final char[] whitespaceChars =
						new char[]{0x09 /* Horizontal Tab */, 0x0A /* Line Feed */, 0x0B /* Vertical Tabulation */, 0x0C
										/* Form Feed */, 0x0D /* Carriage Return */, 0x20 /* Space */,};
		public static boolean isWhitespaceChar(char c) {
				for (int i = 0; i < whitespaceChars.length; i++) {
						if (c == whitespaceChars[i]) { return true; }
				} return false;
		}
		public static boolean isNullOrEmpty(String str) {
				return str == null || str.isEmpty();
		}
		public static boolean isNullOrWhitespace(String str) {
				if (isNullOrEmpty(str)) { return true; } for (char c : str.toCharArray()) {
						if (!isWhitespaceChar(c)) { return false; }
				} return true;
		}
}
