package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Cart;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.request.AddItemRequest;

public interface CartService {
    public Cart createCart(User user);

    public String addCartItem(Long userId, AddItemRequest req) throws ProductExpection;

    public Cart findUserCard(Long userId);


}
