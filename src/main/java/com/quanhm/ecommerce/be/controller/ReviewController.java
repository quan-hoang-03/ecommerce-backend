package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.Review;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.request.ReviewRequest;
import com.quanhm.ecommerce.be.service.ReviewService;
import com.quanhm.ecommerce.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<Review> createReview(@RequestBody ReviewRequest req, @RequestHeader("Authorization") String jwt) throws UserException, ProductExpection {
        User user = userService.findUserProfileByJwt(jwt);
        Review review = reviewService.createReview(req, user);

        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId")
    public ResponseEntity<List<Review>> getProductReview(@PathVariable Long productId) throws UserException, ProductExpection {
        List<Review> reviews = reviewService.getAllReview(productId);
        return new ResponseEntity<>(reviews, HttpStatus.ACCEPTED);
    }
}
