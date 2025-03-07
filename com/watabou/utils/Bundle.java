/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2015 Evan Debenham
 *
 * Unpixel Dungeon
 * Copyright (C) 2015-2016 Randall Foudray
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.watabou.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Bundle {

	private static final String CLASS_NAME = "__className";

	private static HashMap<String, String> aliases = new HashMap<String, String>();

	private JSONObject data;

	public Bundle() {
		this(new JSONObject());
	}

	public String toString() {
		return data.toString();
	}

	private Bundle(JSONObject data) {
		this.data = data;
	}

	public boolean isNull() {
		return data == null;
	}

	public boolean contains(String key) {
		return !data.isNull(key);
	}

	public boolean getBoolean(String key) {
		return data.optBoolean(key);
	}

	public int getInt(String key) {
		return data.optInt(key);
	}

	public long getLong(String key) {
		return data.optLong(key);
	}

	public float getFloat(String key) {
		return (float) data.optDouble(key, 0.0);
	}

	public double getDouble(String key) {
		return data.optDouble(key, 0.0);
	}

	public String getString(String key) {
		return data.optString(key);
	}

	public Bundle getBundle(String key) {
		return new Bundle(data.optJSONObject(key));
	}

	private Bundlable get() {
		if (data == null) return null;
		try {
			String clName = getString(CLASS_NAME);
			if (aliases.containsKey(clName)) {
				clName = aliases.get(clName);
			}

			Class<?> cl = Class.forName(clName);
			if (cl != null) {
				Bundlable object = (Bundlable) cl.newInstance();
				object.restoreFromBundle(this);
				return object;
			} else {
				return null;
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			return null;
		}
	}

	public Bundlable get(String key) {
		return getBundle(key).get();
	}

	public <E extends Enum<E>> E getEnum(String key, Class<E> enumClass) {
		try {
			return Enum.valueOf(enumClass, data.getString(key));
		} catch (JSONException e) {
			return enumClass.getEnumConstants()[0];
		}
	}

	public int[] getIntArray(String key) {
		try {
			JSONArray array = data.getJSONArray(key);
			int length = array.length();
			int[] result = new int[length];
			for (int i = 0; i < length; i++) {
				result[i] = array.getInt(i);
			}
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	public boolean[] getBooleanArray(String key) {
		try {
			JSONArray array = data.getJSONArray(key);
			int length = array.length();
			boolean[] result = new boolean[length];
			for (int i = 0; i < length; i++) {
				result[i] = array.getBoolean(i);
			}
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	public String[] getStringArray(String key) {
		try {
			JSONArray array = data.getJSONArray(key);
			int length = array.length();
			String[] result = new String[length];
			for (int i = 0; i < length; i++) {
				result[i] = array.getString(i);
			}
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	public Collection<Bundlable> getCollection(String key) {

		ArrayList<Bundlable> list = new ArrayList<Bundlable>();

		try {
			JSONArray array = data.getJSONArray(key);
			for (int i = 0; i < array.length(); i++) {
				Bundlable O = new Bundle(array.getJSONObject(i)).get();
				if (O != null) list.add(O);
			}
		} catch (JSONException e) {

		}

		return list;
	}

	public void put(String key, boolean value) {
		try {
			data.put(key, value);
		} catch (JSONException e) {

		}
	}

	public void put(String key, int value) {
		try {
			data.put(key, value);
		} catch (JSONException e) {

		}
	}

	public void put(String key, long value) {
		try {
			data.put(key, value);
		} catch (JSONException e) {

		}
	}

	public void put(String key, float value) {
		try {
			data.put(key, value);
		} catch (JSONException e) {

		}
	}

	public void put(String key, double value) {
		try {
			data.put(key, value);
		} catch (JSONException e) {

		}
	}

	public void put(String key, String value) {
		try {
			data.put(key, value);
		} catch (JSONException e) {

		}
	}

	public void put(String key, Bundle bundle) {
		try {
			data.put(key, bundle.data);
		} catch (JSONException e) {

		}
	}

	public void put(String key, Bundlable object) {
		if (object != null) {
			try {
				Bundle bundle = new Bundle();
				bundle.put(CLASS_NAME, object.getClass().getName());
				object.storeInBundle(bundle);
				data.put(key, bundle.data);
			} catch (JSONException e) {
			}
		}
	}

	public void put(String key, Enum<?> value) {
		if (value != null) {
			try {
				data.put(key, value.name());
			} catch (JSONException e) {
			}
		}
	}

	public void put(String key, int[] array) {
		try {
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < array.length; i++) {
				jsonArray.put(i, array[i]);
			}
			data.put(key, jsonArray);
		} catch (JSONException e) {

		}
	}

	public void put(String key, boolean[] array) {
		try {
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < array.length; i++) {
				jsonArray.put(i, array[i]);
			}
			data.put(key, jsonArray);
		} catch (JSONException e) {

		}
	}

	public void put(String key, String[] array) {
		try {
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < array.length; i++) {
				jsonArray.put(i, array[i]);
			}
			data.put(key, jsonArray);
		} catch (JSONException e) {

		}
	}

	public void put(String key, Collection<? extends Bundlable> collection) {
		JSONArray array = new JSONArray();
		for (Bundlable object : collection) {
			if (object != null) {
				Bundle bundle = new Bundle();
				bundle.put(CLASS_NAME, object.getClass().getName());
				object.storeInBundle(bundle);
				array.put(bundle.data);
			}
		}
		try {
			data.put(key, array);
		} catch (JSONException e) {

		}
	}

	public static Bundle read(InputStream stream) throws IOException {

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			JSONObject json = (JSONObject) new JSONTokener(reader.readLine()).nextValue();
			reader.close();

			return new Bundle(json);
		} catch (Exception e) {
			throw new IOException();
		}
	}

	public static Bundle read(byte[] bytes) {
		try {

			JSONObject json = (JSONObject) new JSONTokener(new String(bytes)).nextValue();
			return new Bundle(json);

		} catch (JSONException e) {
			return null;
		}
	}

	public static boolean write(Bundle bundle, OutputStream stream) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
			writer.write(bundle.data.toString());
			writer.close();

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static void addAlias(Class<?> cl, String alias) {
		aliases.put(alias, cl.getName());
	}
}
