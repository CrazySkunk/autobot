package com.example.autobot1.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Cart-Products")
public class ProductItemCart implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int Id;
    private String title, description, price, imageUrl, quantity, total;

    public ProductItemCart(int Id, String title, String description, String price, String imageUrl, String quantity, String total) {
        this.Id = Id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.total = total;
    }

    protected ProductItemCart(Parcel in) {
        title = in.readString();
        description = in.readString();
        price = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(imageUrl);
        dest.writeString(quantity);
        dest.writeString(total);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductItemCart> CREATOR = new Creator<ProductItemCart>() {
        @Override
        public ProductItemCart createFromParcel(Parcel in) {
            return new ProductItemCart(in);
        }

        @Override
        public ProductItemCart[] newArray(int size) {
            return new ProductItemCart[size];
        }
    };

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
