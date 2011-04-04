/**
 * Example of Android PhoneGap Plugin
 */
package com.talkative;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;

public class TalkativePlugin extends Plugin {
	// list actions
	public static final String INIT = "init";
	public static final String SPEAK = "speak";
	public static final String SET_LANGUAGE = "setLanguage";

	private static final int MY_DATA_CHECK_CODE = 0x1337BABE;

	private TextToSpeech mTts;

	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		PluginResult result = null;
		log("Plugin Called");

		if (INIT.equals(action)) {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			ctx.startActivityForResult(this, checkIntent, MY_DATA_CHECK_CODE);
			result = new PluginResult(Status.OK);
		} else {
			if (!initialized()) {
				log("Not initialized for action " + action);
				result = new PluginResult(Status.INSTANTIATION_EXCEPTION);
			}
			try {
				if (SPEAK.equals(action)) {
					String text = data.getString(0);
					mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

					result = new PluginResult(Status.OK);
				} else if (SET_LANGUAGE.equals(action)) {
					String lang = data.getString(0);
					Locale locale = new Locale(lang);
					if (mTts.isLanguageAvailable(locale) != TextToSpeech.LANG_NOT_SUPPORTED) {
						mTts.setLanguage(locale);
					} else {
						result = new PluginResult(Status.ERROR, "Language not available");
					}

				} else {
					result = new PluginResult(Status.INVALID_ACTION);
					log("Invalid action : " + action + " passed");
				}
			} catch (JSONException jsonEx) {
				Log.d(getClass().toString(), "Got JSON Exception " + jsonEx.getMessage());
				result = new PluginResult(Status.JSON_EXCEPTION);
			}
		}
		return result;
	}

	private void log(String message) {
		Log.d(getClass().toString(), message);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				OnInitListener listener = new OnInitListener() {
					public void onInit(int status) {

					}
				};
				// success, create the TTS instance
				mTts = new TextToSpeech(ctx, listener);
				mTts.setLanguage(Locale.ENGLISH);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				ctx.startActivity(installIntent);
			}
		}
	}

	private boolean initialized() {
		return mTts != null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mTts != null) {
			mTts.shutdown();
		}
	}

}
