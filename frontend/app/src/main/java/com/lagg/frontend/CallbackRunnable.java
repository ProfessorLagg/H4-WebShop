package com.lagg.frontend;

public class CallbackRunnable implements Runnable {
		public final Runnable runnable;
		public final Runnable callback;

		public CallbackRunnable(Runnable runnable, Runnable callback) {
				this.runnable = runnable;
				this.callback = callback;
		}

		@Override
		public void run() {
				runnable.run();
				callback.run();
		}
}
