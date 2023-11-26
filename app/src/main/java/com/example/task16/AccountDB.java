package com.example.task16;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;


@Database(entities = {Account.class}, version = 1)
public abstract class AccountDB extends RoomDatabase {

    private static AccountDB instance;

    public abstract AccountDao accountDao();

    public static synchronized AccountDB getInstance(Context context) {
        if (instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AccountDB.class, "account_database").fallbackToDestructiveMigration().build();
        }
        return instance;
    }


}
