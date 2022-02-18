package com.colman.bar.admoni.a3rs.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class SerializableLatLng implements Serializable {
    public final double latitude;
    public final double longitude;

    public SerializableLatLng(LatLng latLng) {
        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }

    public LatLng to() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SerializableLatLng {");
        sb.append("\n\tlatitude=");
        sb.append(latitude);

        sb.append("\n\tlongitude=");
        sb.append(longitude);

        sb.append("\n}");

        return sb.toString();
    }
}
