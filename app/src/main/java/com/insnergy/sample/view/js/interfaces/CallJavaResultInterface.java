package com.insnergy.sample.view.js.interfaces;

/**
 * Used in JavaScriptInterface to interact with JsRunner
 */
public interface CallJavaResultInterface {
	void jsCallFinished(String value, Integer callIndex);
}
