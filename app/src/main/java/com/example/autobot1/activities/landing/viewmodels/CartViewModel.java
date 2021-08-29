package com.example.autobot1.activities.landing.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.autobot1.db.CartDao;
import com.example.autobot1.db.CartRepository;
import com.example.autobot1.db.DataBaseInstance;
import com.example.autobot1.models.ProductItemCart;

import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private CartDao cartDao;
    private CartRepository repository;
    public CartViewModel(@NonNull Application application) {
        super(application);
        cartDao = DataBaseInstance.getInstance(application).getCartDao();
        repository = new CartRepository(cartDao);
    }

    public LiveData<List<ProductItemCart>>getAllProducts(){
        return repository.getAllCartItems();
    }
    public void addCartItem(ProductItemCart productItemCart){
        Thread thread = new Thread(() -> repository.addProduct(productItemCart));
        thread.start();
    }
    public void updateCartProduct(ProductItemCart productItemCart){
        new Thread(()-> repository.updateCartItem(productItemCart));
    }
    public void deleteCartProduct(ProductItemCart productItemCart){
        new Thread(()->repository.deleteCartProduct(productItemCart));
    }

    public void deleteAllCartProducts(){
        new Thread(()-> repository.deleteAllCartProducts());
    }

}
