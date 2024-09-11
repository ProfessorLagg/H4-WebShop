package com.lagg.frontend.net;

import android.util.Log;

import com.lagg.frontend.Utils;

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
		public void callback(UrlRequest request, UrlResponseInfo info, byte[] bodyBytes);
	}

	public enum CallbackState {
		WAITING,
		REDIRECT_RECEIVED,
		RESPONSE_STARTED,
		READ_COMPLETED,
		SUCCEEDED,
		FAILED,
	}

	private ByteBuffer bytes;
	public final CallbackFunction callbackFunc;
	private CallbackState state;

	public CallbackState getState() {
		return this.state;
	}

	public boolean finished() {
		return this.state == CallbackState.FAILED || this.state == CallbackState.SUCCEEDED;
	}

	public GenericRequestCallback(CallbackFunction successCallback) {
		this.callbackFunc = successCallback;
		this.state = CallbackState.WAITING;
		this.bytes = ByteBuffer.allocateDirect(102400);
		Log.d(TAG, "constructor");
	}

	private byte[] getBodyBytes() {
		byte[] buffer = this.bytes.array();
		int s = 0;
		int e;
		while (buffer[s] == 0 && s < buffer.length) {
			s++;
		}
		e = s + 1;
		while (buffer[s] != 0 && e < buffer.length) {
			e++;
		}
		return Utils.slice(buffer, s, e);
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
		request.read(this.bytes);
	}

	@Override
	public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer buffer) {
		LogDebug("onReadCompleted", request, info);
		this.state = CallbackState.READ_COMPLETED;
		// Response body is available.
		// Let's tell Cronet to continue reading the response body or
		// inform us that the response is complete!
		request.read(buffer);
	}

	@Override
	public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
		LogDebug("onSucceeded", request, info);
		this.state = CallbackState.SUCCEEDED;
		callbackFunc.callback(request, info, this.getBodyBytes());
	}

	@Override
	public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException e) {
		LogError("onFailed", info, e);
		this.state = CallbackState.FAILED;
		callbackFunc.callback(request, info, new byte[0]);
	}
}
