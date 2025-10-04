package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.CartItemException;
import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.Cart;
import com.quanhm.ecommerce.be.model.CartItem;
import com.quanhm.ecommerce.be.model.Product;

public interface CartItemService {
    public CartItem createCartItem(CartItem cartItem);

    public CartItem updateCartItem(Long userId,Long id, CartItem cartItem) throws CartItemException, UserException;

    public CartItem isCartItemExist(Cart cart, Product product, String size, Long userId);

    public void deleteCartItem(Long userId, Long cartItemId) throws CartItemException,UserException;

    public CartItem findCartItemById(Long cartItemId) throws CartItemException;
}
