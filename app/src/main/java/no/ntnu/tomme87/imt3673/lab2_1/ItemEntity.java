package no.ntnu.tomme87.imt3673.lab2_1;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.einmalfel.earl.Item;

/**
 * Created by Tomme on 02.03.2018.
 */

@Entity
public class ItemEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "link")
    private String link;

    @ColumnInfo(name = "description")
    private String description;

    public ItemEntity() {}

    public ItemEntity(Item item) {
        this.title = item.getTitle();
        this.link = item.getLink();
        this.description = item.getDescription();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
