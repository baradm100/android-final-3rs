package com.colman.bar.admoni.a3rs.models;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String fromSerializableLatLng(SerializableLatLng value) {
        return value.latitude + "," + value.longitude;
    }

    @TypeConverter
    public static SerializableLatLng toSerializableLatLng(String value) {
        String[] vals = value.split(",");
        long latitude = Long.parseLong(vals[0]);
        long longitude = Long.parseLong(vals[1]);

        return new SerializableLatLng(new LatLng(latitude, longitude));
    }


}
