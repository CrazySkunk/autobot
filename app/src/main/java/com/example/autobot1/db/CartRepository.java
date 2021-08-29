package com.example.autobot1.db;

import androidx.lifecycle.LiveData;

import com.example.autobot1.models.ProductItemCart;

import java.util.List;

public class CartRepository {
    private final CartDao cartDao;

    public CartRepository(CartDao cartDao) {
        this.cartDao = cartDao;
    }
    public LiveData<List<ProductItemCart>>getAllCartItems(){
        return cartDao.getAllCartProducts();
    }
    public void addProduct(ProductItemCart productItemCart){
        cartDao.addProduct(productItemCart);
    }
    public void updateCartItem(ProductItemCart productItemCart){
        cartDao.updateCartProduct(productItemCart);
    }

    public void deleteCartProduct(ProductItemCart productItemCart){
        cartDao.deleteCartProduct(productItemCart);
    }

    public void deleteAllCartProducts(){
        cartDao.deleteAllCartProducts();
    }

}
