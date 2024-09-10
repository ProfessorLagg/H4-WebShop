package com.lagg.frontend.net;

import android.util.Log;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.nio.ByteBuffer;

public class GenericRequestCallback extends UrlRequest.Callback {
		private static final String TAG = "GenericRequestCallback";

		private static void LogDebug(String method, UrlRequest request, UrlResponseInfo info) {
				String msg = method + ':';
				if (info != null) {
						msg += "\n\tinfo: " + info.getUrl() + ' ' + info.getHttpStatusText() + "\n";
				}
				if (request != null) {
						msg += "\n\trequest: " + request;
				}
				Log.d(TAG, msg);
		}
		private static void LogError(String method, UrlResponseInfo info, Exception e) {
				String msg = method + ':';
				if (info != null) {
						msg += "\n\tinfo: " + info.getUrl() + ' ' + info.getHttpStatusText() + "\n";
				}
				Log.e(TAG, msg, e);
		}

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

		public final CallbackFunction callbackFunc;
		private CallbackState state;
		public CallbackState getState() { return this.state; }
		public boolean finished() {
				return this.state == CallbackState.FAILED || this.state == CallbackState.SUCCEEDED;
		}
		public GenericRequestCallback(CallbackFunction successCallback) {
				this.callbackFunc = successCallback;
				this.state = CallbackState.WAITING;
				Log.d(TAG, "constructor");
		}

		@Override
		public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
				LogDebug("onRedirectReceived", request, info);
				this.state = CallbackState.REDIRECT_RECEIVED;

				// You should call the request.followRedirect() method to continue
				// processing the request.
				request.followRedirect();
		}

		@Override
		public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
				LogDebug("onResponseStarted", request, info);
				this.state = CallbackState.RESPONSE_STARTED;
				// You should call the request.read() method before the request can be
				// further processed. The following instruction provides a ByteBuffer object
				// with a capacity of 102400 bytes for the read() method. The same buffer
				// with data is passed to the onReadCompleted() method.
				request.read(ByteBuffer.allocateDirect(102400));
		}

		@Override
		public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
				LogDebug("onReadCompleted", request, info);
				this.state = CallbackState.READ_COMPLETED;
				// You should keep reading the request until there's no more data.
				byteBuffer.clear();
				request.read(byteBuffer);
		}

		@Override
		public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
				LogDebug("onSucceeded", request, info);
				this.state = CallbackState.SUCCEEDED;
				callbackFunc.callback(request, info);
		}

		@Override
		public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException e) {
				LogError("onFailed", info, e);
				this.state = CallbackState.FAILED;
				callbackFunc.callback(request, info);
		}
}
