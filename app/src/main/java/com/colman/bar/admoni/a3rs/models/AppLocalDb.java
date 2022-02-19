package com.colman.bar.admoni.a3rs.models;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.colman.bar.admoni.a3rs.MyApplication;

@Database(entities = {Post.class}, version = 2)
@TypeConverters({Converters.class})
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract PostDao postDao();
}

public class AppLocalDb {
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "dbFileName.db")
                    .fallbackToDestructiveMigration()
                    .build();


}
