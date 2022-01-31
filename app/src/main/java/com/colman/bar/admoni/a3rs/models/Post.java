package com.colman.bar.admoni.a3rs.models;

import com.colman.bar.admoni.a3rs.utils.Convertors;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post implements Serializable {
    private String title;
    private String subTitle;
    private String description;
    private String userName;
    private String userPhone;
    private String userUid;
    private Date createdAt;
    private String addressName;
    private SerializableLatLng geoPoint;

    public static Post from(Map<String, Object> data) {
        String title = getValueFromMapSafely(data, "title");
        String subTitle = getValueFromMapSafely(data, "subTitle");
        String description = getValueFromMapSafely(data, "description");
        String userName = getValueFromMapSafely(data, "userName");
        String userPhone = getValueFromMapSafely(data, "userPhone");
        String userUid = getValueFromMapSafely(data, "userUid");
        Date createdAt = getDateFromMapSafely(data, "createdAt");
        String addressName = getValueFromMapSafely(data, "addressName");
        SerializableLatLng geoPoint = Convertors.convertGeoPointToLatLng(getGeoPointFromMapSafely(data, "geoPoint"));

        return new Post(title, subTitle, description, userName, userPhone, userUid, createdAt, addressName, geoPoint);
    }

    private static GeoPoint getGeoPointFromMapSafely(Map<String, Object> data, String key) {
        if (data.containsKey(key) && data.get(key) != null) {
            return (GeoPoint) data.get(key);
        }

        return null;

    }

    private static String getValueFromMapSafely(Map<String, Object> data, String key) {
        if (data.containsKey(key) && data.get(key) != null) {
            return data.get(key).toString();
        }

        return null;
    }

    private static Date getDateFromMapSafely(Map<String, Object> data, String key) {
        if (data.containsKey(key) && data.get(key) != null) {
            return ((Timestamp) data.get(key)).toDate();
        }

        return null;
    }

    public Post(String title, String subTitle, String description, String userName, String userPhone,
                String userUid, Date createdAt, String addressName, SerializableLatLng geoPoint) {
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userUid = userUid;
        this.createdAt = createdAt;
        this.addressName = addressName;
        this.geoPoint = geoPoint;
    }

    public Map<String, Object> to() {
        Map<String, Object> data = new HashMap<>();

        data.put("title", title);
        data.put("subTitle", subTitle);
        data.put("description", description);
        data.put("userName", userName);
        data.put("userPhone", userPhone);
        data.put("userUid", userUid);
        data.put("addressName", addressName);
        data.put("geoPoint", Convertors.convertSerializableLatLngToGeoPoint(geoPoint));

        if (createdAt == null) {
            data.put("createdAt", FieldValue.serverTimestamp());
        } else {
            data.put("createdAt", createdAt);
        }

        return data;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserUid() {
        return userUid;
    }

    public SerializableLatLng getGeoPoint() {
        return geoPoint;
    }

    public String getAddressName() {
        return addressName;
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

        sb.append("\n\tcreatedAt=");
        sb.append(createdAt);


        sb.append("\n}");


        return sb.toString();
    }
}
