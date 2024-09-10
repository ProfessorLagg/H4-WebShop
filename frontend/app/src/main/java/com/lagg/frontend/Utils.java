package com.lagg.frontend;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
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
				public boolean isStarted() { return this.started; }

				private boolean finished;
				public boolean isFinished() { return this.finished; }

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
				while (!syncRunnable.isStarted() && !syncRunnable.isFinished()) { }
		}

		public static String getUrlString(String host, String path, boolean useHttps) {
				return getUrlString(host, path, useHttps, null);
		}
		public static String getUrlString(String host, String path, boolean useHttps,
																			Map<String, String> urlParams) {
				String h = Utils.trim(host.trim(), '/');
				String p = Utils.trim(path.trim(), '/');
				String url = (useHttps ? "https://" : "http://") + h + '/' + p;

				if (urlParams != null && !urlParams.isEmpty()) {
						url += getParamsString(urlParams, false);
				}

				return url;
		}

		public static String getParamsString(Map<String, String> parameters) { return getParamsString(parameters, false); }
		public static String getParamsString(Map<String, String> parameters, boolean urlEncode) {
				StringBuilder sb = new StringBuilder();
				char prependChar = '?';
				for (String k : parameters.keySet()) {
						String v = parameters.get(k);
						sb.append(prependChar);
						sb.append(k);
						sb.append('=');
						sb.append(v);
						if (prependChar == '?') { prependChar = '&'; }
				}
				String paramsString = sb.toString();
				String result = paramsString;
				if (urlEncode) {
						// SAFE TO IGNORE UnsupportedEncodingException
						try { result = URLEncoder.encode(paramsString, "UTF-8"); } catch (UnsupportedEncodingException e) { }
				}

				return result;
		}
}
