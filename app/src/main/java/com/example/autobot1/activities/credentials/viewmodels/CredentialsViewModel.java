package com.example.autobot1.activities.credentials.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.autobot1.db.AccountTypeDao;
import com.example.autobot1.db.AccountTypeRepository;
import com.example.autobot1.db.DataBaseInstance;
import com.example.autobot1.models.AccountType;

import java.util.List;

public class CredentialsViewModel extends AndroidViewModel {
    private AccountTypeDao accountTypeDao;
    final private AccountTypeRepository accountTypeRepository;
    public CredentialsViewModel(@NonNull Application application) {
        super(application);
        accountTypeDao = DataBaseInstance.getInstance(application).getAccountTypeDao();
        accountTypeRepository = new AccountTypeRepository(accountTypeDao);
    }

    public void addAccountType(AccountType accountType){
        new Thread(()->accountTypeRepository.addAccountType(accountType)).start();

    }
    public List<AccountType>getAccountType(){
        return accountTypeRepository.getAccountType();
    }

    public void deleteAccountType(AccountType accountType){
        new Thread(accountTypeRepository::deleteAccountType).start();
    }
}
