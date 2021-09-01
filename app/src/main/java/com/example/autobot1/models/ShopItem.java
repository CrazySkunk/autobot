package com.example.autobot1.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;

import com.google.android.gms.maps.model.LatLng;


public class ShopItem implements Parcelable {
    private String title, description,imageUrl,contact;
    private LatLng location;

    public ShopItem(String title, LatLng location, String description,String imageUrl,String contact) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;
        this.contact = contact;
    }


    protected ShopItem(Parcel in) {
        title = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        contact = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(contact);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShopItem> CREATOR = new Creator<ShopItem>() {
        @Override
        public ShopItem createFromParcel(Parcel in) {
            return new ShopItem(in);
        }

        @Override
        public ShopItem[] newArray(int size) {
            return new ShopItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
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
}
