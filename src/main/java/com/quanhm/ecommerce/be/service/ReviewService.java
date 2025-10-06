package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Review;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.request.ReviewRequest;

import java.util.List;

public interface ReviewService {
    public Review createReview(ReviewRequest req, User user) throws ProductExpection;
    public List<Review> getAllReview(Long productId);

}
