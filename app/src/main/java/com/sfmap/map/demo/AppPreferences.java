package com.sfmap.map.demo;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppPreferences {
    public static class PreferenceKey {
        public final static String SP_NAME_NAME = "cs_collection";
    }

    private static AppPreferences appPreferences;
    private SharedPreferences preferences;

    private AppPreferences() {
        preferences = mApplication.sharedPreferences;
    }

    public static AppPreferences instance() {
        if (appPreferences == null) {
            appPreferences = new AppPreferences();
        }
        return appPreferences;
    }

    public void put(String keyName, Object value) {
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof String) {
            editor.putString(keyName, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(keyName, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(keyName, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(keyName, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(keyName, (Long) value);
        } else {
            editor.putString(keyName, value.toString());
        }
        editor.apply();
    }

    public Object get(String keyName, Object defaultValue) {
        if (defaultValue instanceof String) {
            return preferences.getString(keyName, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return preferences.getInt(keyName, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return preferences.getBoolean(keyName, (Boolean) defaultValue);
        } else if (defaultValue instanceof Float) {
            return preferences.getFloat(keyName, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            return preferences.getLong(keyName, (Long) defaultValue);
        }
        return null;
    }

    /**
     * 保存一个实体类，类名为key
     */
    public void putObject(Object obj) {
        putObject(obj.getClass().getName(), obj);
    }

    /**
     * 获取一个存储实体类
     */
    public <T> T getObject(Class<T> c) {
        return getObject(c.getName(), c);
    }

    /**
     * 保存一个实体类
     *
     * @param key    key
     * @param object object
     */
    public void putObject(String key, Object object) {
        if (object == null) {
            return;
        }
        String value = (new Gson()).toJson(object);
        preferences.edit().putString(key, value).apply();
    }

    /**
     * 获取一个存储实体类
     *
     * @param key key
     * @param c   c
     * @param <T> T
     * @return <T> T
     */
    public <T> T getObject(String key, Class<T> c) {
        String value = preferences.getString(key, "");
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        T t = (new Gson()).fromJson(value, c);
        return t;
    }

    /**
     * 保存List
     *
     * @param key      key
     * @param datalist list
     */
    public <T> void putDataList(String key, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0) {
            return;
        }
        Gson gson = new Gson();
        // 转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        preferences.edit().putString(key, strJson).apply();
    }

    /**
     * 获取List
     *
     * @param key key
     * @return List
     */
    public <T> List<T> getDataList(String key, Class<T> clazz) {
        String strJson = preferences.getString(key, null);
        if (null == strJson) {
            return null;
        }
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(strJson, type);
        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;

    }

    /**
     * 移除某个key值对应的值
     *
     * @param key key
     */
    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }

    /**
     * 清除所有数据
     */
    public void clearAll() {
        preferences.edit().clear().apply();
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key key
     * @return boolean
     */
    public boolean contains(String key) {
        return preferences.contains(key);
    }

    /**
     * 获取所有的键值对
     *
     * @return 所有的键值对
     */
    public Map<String, ?> getAllKeyValue() {
        return preferences.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }}
