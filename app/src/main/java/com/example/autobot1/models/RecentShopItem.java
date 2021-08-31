package com.example.autobot1.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Recent-shops-table")
public class RecentShopItem implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    int id;
    private String title, description,imageUrl,contact;
    private String latitude,longitude;
    private boolean isFav = false;

    public RecentShopItem(int id, String title, String description, String imageUrl, String contact, String latitude, String longitude, boolean isFav) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isFav = isFav;
    }


    protected RecentShopItem(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        contact = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        isFav = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(contact);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeByte((byte) (isFav ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecentShopItem> CREATOR = new Creator<RecentShopItem>() {
        @Override
        public RecentShopItem createFromParcel(Parcel in) {
            return new RecentShopItem(in);
        }

        @Override
        public RecentShopItem[] newArray(int size) {
            return new RecentShopItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }
}
