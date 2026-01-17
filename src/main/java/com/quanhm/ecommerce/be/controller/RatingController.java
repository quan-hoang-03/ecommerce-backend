package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.Rating;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.request.RatingRequest;
import com.quanhm.ecommerce.be.service.RatingService;
import com.quanhm.ecommerce.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rating")
public class RatingController {
    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService;

    @PostMapping("/add")
    public ResponseEntity<Rating> createRating(@RequestBody RatingRequest req, @RequestHeader("Authorization") String jwt) throws UserException, ProductExpection{
        User user = userService.findUserProfileByJwt(jwt);
        Rating rating = ratingService.createRating(req, user);
        return new ResponseEntity<Rating>(rating, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Rating>> getProductRating(@PathVariable Long productId, @RequestHeader(value = "Authorization", required = false) String jwt) throws UserException, ProductExpection{
        // JWT không bắt buộc để xem ratings
        List<Rating> ratings = ratingService.getProductRating(productId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }
}
