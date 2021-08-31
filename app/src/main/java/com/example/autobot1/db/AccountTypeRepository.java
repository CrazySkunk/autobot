package com.example.autobot1.db;

import com.example.autobot1.models.AccountType;

import java.util.List;

public class AccountTypeRepository {
    final AccountTypeDao accountTypeDao;

    public AccountTypeRepository(AccountTypeDao accountTypeDao) {
        this.accountTypeDao = accountTypeDao;
    }

    public void addAccountType(AccountType accountType){
        accountTypeDao.insertAccountType(accountType);
    }

    public List<AccountType>getAccountType(){
        return accountTypeDao.getAccountType();
    }

    public void deleteAccountType(){
        accountTypeDao.clearAccountTypeTable();
    }

}
