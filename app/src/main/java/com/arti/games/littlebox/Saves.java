package com.arti.games.littlebox;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Saves {
	SharedPreferences loader;

	public Saves(Context context) {
		loader = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void write(String key, int value) {
		Editor edit = loader.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	public int read(String key) {
		return loader.getInt(key, 0);
	}

}
