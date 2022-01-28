package com.colman.bar.admoni.a3rs.models;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private String title;
    private String subTitle;
    private String description;
    private String userName;
    private String userPhone;
    private String userUid;

    public static Post from(Map<String, Object> data) {
        String title = getValueFromMapSafely(data, "title");
        String subTitle = getValueFromMapSafely(data, "subTitle");
        String description = getValueFromMapSafely(data, "description");
        String userName = getValueFromMapSafely(data, "userName");
        String userPhone = getValueFromMapSafely(data, "userPhone");
        String userUid = getValueFromMapSafely(data, "userUid");

        return new Post(title, subTitle, description, userName, userPhone, userUid);
    }

    private static String getValueFromMapSafely(Map<String, Object> data, String key) {
        if (data.containsKey(key) && data.get(key) != null) {
            return data.get(key).toString();
        }

        return null;
    }

    public Post(String title, String subTitle, String description, String userName, String userPhone, String userUid) {
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userUid = userUid;
    }

    public Map<String, Object> to() {
        Map<String, Object> data = new HashMap<>();

        data.put("title", title);
        data.put("subTitle", subTitle);
        data.put("description", description);
        data.put("userName", userName);
        data.put("userPhone", userPhone);
        data.put("userUid", userUid);

        return data;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Post {");
        sb.append("\n\ttitle=");
        sb.append(title);

        sb.append("\n\tsubTitle=");
        sb.append(subTitle);

        sb.append("\n\tdescription=");
        sb.append(description);

        sb.append("\n\tuserName=");
        sb.append(userName);

        sb.append("\n\tuserPhone=");
        sb.append(userPhone);

        sb.append("\n\tuserUid=");
        sb.append(userUid);


        sb.append("\n}");


        return sb.toString();
    }
}
