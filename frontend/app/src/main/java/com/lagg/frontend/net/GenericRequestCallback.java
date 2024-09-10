package com.lagg.frontend.net;

import android.util.Log;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.nio.ByteBuffer;

public class GenericRequestCallback extends UrlRequest.Callback {
		private static final String TAG = "GenericRequestCallback";

		@FunctionalInterface
		public interface CallbackFunction {
				public void callback(UrlRequest request, UrlResponseInfo info);
		}

		public enum CallbackState {
				WAITING,
				REDIRECT_RECEIVED,
				RESPONSE_STARTED,
				READ_COMPLETED,
				SUCCEEDED,
				FAILED,
		}

		public final CallbackFunction successCallback;
		private CallbackState state;
		public CallbackState getState() { return this.state; }
		public GenericRequestCallback(CallbackFunction successCallback) {
				this.successCallback = successCallback;
				this.state = CallbackState.WAITING;
		}

		@Override
		public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
				this.state = CallbackState.REDIRECT_RECEIVED;
				Log.i(TAG, "onRedirectReceived method called.");
				// You should call the request.followRedirect() method to continue
				// processing the request.
				request.followRedirect();
		}

		@Override
		public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
				this.state = CallbackState.RESPONSE_STARTED;
				Log.i(TAG, "onResponseStarted method called.");
				// You should call the request.read() method before the request can be
				// further processed. The following instruction provides a ByteBuffer object
				// with a capacity of 102400 bytes for the read() method. The same buffer
				// with data is passed to the onReadCompleted() method.
				request.read(ByteBuffer.allocateDirect(102400));
		}

		@Override
		public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
				this.state = CallbackState.READ_COMPLETED;
				Log.i(TAG, "onReadCompleted method called.");
				// You should keep reading the request until there's no more data.
				byteBuffer.clear();
				request.read(byteBuffer);
		}

		@Override
		public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
				this.state = CallbackState.SUCCEEDED;
				Log.i(TAG, "onSucceeded method called.");
				successCallback.callback(request, info);
		}

		@Override
		public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
				this.state = CallbackState.FAILED;
				Log.e(TAG, "request failed: " + request, error);
		}
}
