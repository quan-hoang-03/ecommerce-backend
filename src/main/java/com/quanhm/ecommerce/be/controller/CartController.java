package com.quanhm.ecommerce.be.controller;


import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.Cart;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.request.AddItemRequest;
import com.quanhm.ecommerce.be.response.ApiResponse;
import com.quanhm.ecommerce.be.service.CartService;
import com.quanhm.ecommerce.be.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name="Cart Management",description="Tìm giỏ hàng của người dùng, thêm sản phẩm vào giỏ hàng")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("/getAllCart")
//    @Operation(description="Tìm giỏ hàng bằng id của người dùng")
    public ResponseEntity<Cart>findUserCart(@RequestHeader("Authorization") String jwt) throws UserException{
        User user = userService.findUserProfileByJwt(jwt);
        Cart cart = cartService.findUserCart(user.getId());
        return new ResponseEntity<Cart>(cart, HttpStatus.OK);
    }

    @PostMapping("/add")
    @Operation(description = "Thêm vào giỏ hàng")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestBody AddItemRequest req, @RequestHeader("Authorization") String jwt) throws UserException, ProductExpection {
        User user = userService.findUserProfileByJwt(jwt);
        cartService.addCartItem(user.getId(), req);

        ApiResponse res = new ApiResponse();
        res.setMessage("Đã thêm sản phẩm vào giỏ hàng");
        res.setSuccess(true);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

}
