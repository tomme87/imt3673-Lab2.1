package no.ntnu.tomme87.imt3673.lab2_1;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Tomme on 02.03.2018.
 */

@Dao
public interface ItemDao {
    @Query("Select * FROM itementity")
    List<ItemEntity> getAll();

    @Query("DELETE FROM itementity")
    void deleteAll();

    @Insert
    void insert(List<ItemEntity> items);
}
