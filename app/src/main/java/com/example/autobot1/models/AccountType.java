package com.example.autobot1.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Account-type_table")
public class AccountType {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String accountType;

    public AccountType(int id, String accountType) {
        this.id = id;
        this.accountType = accountType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
