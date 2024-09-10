package com.lagg.frontend;

import java.util.concurrent.Executor;

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


}
