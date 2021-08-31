package com.example.autobot1.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.autobot1.models.AccountType;

import java.util.List;

@Dao
public interface AccountTypeDao {
    @Insert
    public void insertAccountType(AccountType accountType);

    @Query("SELECT * FROM `Account-type_table`")
    public List<AccountType> getAccountType();

    @Query("DELETE FROM `account-type_table`")
    public void clearAccountTypeTable();
}
