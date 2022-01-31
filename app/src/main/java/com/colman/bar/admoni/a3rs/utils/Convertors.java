package com.colman.bar.admoni.a3rs.utils;

import com.colman.bar.admoni.a3rs.models.SerializableLatLng;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

public class Convertors {

    public static GeoPoint convertSerializableLatLngToGeoPoint(SerializableLatLng latLng) {
        if (latLng == null) {
            return null;
        }
        return new GeoPoint(latLng.latitude, latLng.longitude);
    }

    public static SerializableLatLng convertGeoPointToLatLng(GeoPoint geoPoint) {
        if (geoPoint == null) {
            return null;
        }

        return new SerializableLatLng(new LatLng(geoPoint.getLatitude(), geoPoint.getLatitude()));

    }
}
