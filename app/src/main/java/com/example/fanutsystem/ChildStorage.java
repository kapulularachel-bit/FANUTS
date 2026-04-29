package com.example.fanutsystem;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ChildStorage {
    private static final String PREF_NAME = "child_prefs";
    private static final String KEY_CHILDREN = "children_list";

    public static void saveChild(Context context, Child child) {
        List<Child> children = getChildren(context);
        children.add(child);
        saveChildrenList(context, children);
    }

    public static List<Child> getChildren(Context context) {
        List<Child> children = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_CHILDREN, null);

        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    children.add(new Child(
                        obj.getString("name"),
                        obj.getString("dob"),
                        obj.getString("gender"),
                        obj.getString("muac"),
                        obj.getString("weight"),
                        obj.getString("height")
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return children;
    }

    private static void saveChildrenList(Context context, List<Child> children) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        JSONArray array = new JSONArray();
        try {
            for (Child child : children) {
                JSONObject obj = new JSONObject();
                obj.put("name", child.getName());
                obj.put("dob", child.getDob());
                obj.put("gender", child.getGender());
                obj.put("muac", child.getMuac());
                obj.put("weight", child.getWeight());
                obj.put("height", child.getHeight());
                array.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        prefs.edit().putString(KEY_CHILDREN, array.toString()).apply();
    }
}
