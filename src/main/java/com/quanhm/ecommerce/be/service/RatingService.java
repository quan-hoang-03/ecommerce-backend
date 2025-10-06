package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Rating;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.request.RatingRequest;
import org.springframework.stereotype.Service;

import java.util.List;


public interface RatingService {
    public Rating createRating(RatingRequest req, User user) throws ProductExpection;
    public List<Rating> getProductRating(Long productId);

}
