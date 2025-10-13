package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Cart;
import com.quanhm.ecommerce.be.model.CartItem;
import com.quanhm.ecommerce.be.model.Product;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.repository.CartRepository;
import com.quanhm.ecommerce.be.request.AddItemRequest;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImplementation implements CartService {

    private CartRepository cartRepository;
    private CartItemService cartItemService;
    private ProductService productService;

    public CartServiceImplementation(CartRepository cartRepository, CartItemService cartItemService, ProductService productService){
        this.cartRepository = cartRepository;
        this.cartItemService = cartItemService;
        this.productService = productService;
    }

    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    @Override
    public String addCartItem(Long userId, AddItemRequest req) throws ProductExpection {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            // Tạo giỏ hàng mới cho user
            User user = new User();
            user.setId(userId);
            cart = createCart(user);
        }
        // Lấy thông tin sản phẩm
        Product product = productService.findProductById(req.getProductId());
        // Kiểm tra xem sản phẩm này đã tồn tại trong giỏ hàng chưa
        CartItem isPresent = cartItemService.isCartItemExist(cart,product,req.getSize(),userId);
        if(isPresent == null){
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setSize(req.getSize());
            cartItem.setQuantity(req.getQuantity());
            cartItem.setUserId(userId);
            int price = req.getQuantity()*product.getDiscountPrice();
            cartItem.setPrice(price);
            CartItem createdCartItem = cartItemService.createCartItem(cartItem);
            cart.getCartItems().add(createdCartItem);
            cartItemService.createCartItem(cartItem);
        }
        return "Sản phẩm đã được thêm vào giỏ hàng";
    }

    @Override
    public Cart findUserCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        int totalPrice = 0;
        int totalDiscountedPrice = 0;
        int totalItem = 0;

        for(CartItem cartItem : cart.getCartItems()){
            totalPrice =totalPrice + cartItem.getPrice();
            totalDiscountedPrice = totalDiscountedPrice + cartItem.getDiscountedPrice();
            totalItem = totalItem + cartItem.getQuantity();
        }
        cart.setTotalPrice(totalPrice);
        cart.setTotalDiscountedPrice(totalDiscountedPrice);
        cart.setTotalItem(totalItem);
        cart.setDiscount(totalPrice-totalDiscountedPrice);
        return cartRepository.save(cart);
    }
}
