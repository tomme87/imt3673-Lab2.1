package no.ntnu.tomme87.imt3673.lab2_1;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Tomme on 02.03.2018.
 * Inspired by: https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#6
 */
@Database(entities = {ItemEntity.class}, version = 1)
public abstract class ItemDatabase extends RoomDatabase {
    public abstract ItemDao itemDao();

    private static ItemDatabase INSTANCE;

    static ItemDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ItemDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ItemDatabase.class, "item_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static void destroyInstance() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
}
