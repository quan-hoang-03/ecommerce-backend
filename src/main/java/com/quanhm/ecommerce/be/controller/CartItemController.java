package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.exception.CartItemException;
import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.CartItem;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.response.ApiResponse;
import com.quanhm.ecommerce.be.service.CartItemService;
import com.quanhm.ecommerce.be.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart_items")
public class CartItemController {
    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private UserService userService;

    @PostMapping("/delete/{cartItemId}")
    @Operation(description = "Remove Cart Item from Cart")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Delete Item")
    public ResponseEntity<ApiResponse> deleteCartItem(
            @PathVariable Long cartItemId,
            @RequestHeader("Authorization") String jwt
    ) throws UserException, CartItemException {

        User user = userService.findUserProfileByJwt(jwt);
        cartItemService.deleteCartItem(user.getId(), cartItemId);

        ApiResponse res = new ApiResponse();
        res.setMessage("Đã xóa sản phẩm khỏi giỏ hàng");
        res.setSuccess(true);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/update/{cartItemId}")
    @Operation(description = "Update Item To Cart")
    public ResponseEntity<CartItem> updateCartItem(
            @RequestBody CartItem cartItem,
            @PathVariable Long cartItemId,
            @RequestHeader("Authorization") String jwt)
            throws UserException, CartItemException {

        User user = userService.findUserProfileByJwt(jwt);

        CartItem updatedCartItem = cartItemService.updateCartItem(user.getId(), cartItemId, cartItem);

        return new ResponseEntity<>(updatedCartItem, HttpStatus.OK);
    }

    @PostMapping("/clear")
    @Operation(description = "Clear All Cart Items")
    public ResponseEntity<ApiResponse> clearCart(
            @RequestHeader("Authorization") String jwt)
            throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        cartItemService.clearCart(user.getId());

        ApiResponse res = new ApiResponse();
        res.setMessage("Đã xóa tất cả sản phẩm khỏi giỏ hàng");
        res.setSuccess(true);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
