package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.ProductExpection;
import com.quanhm.ecommerce.be.model.Product;
import com.quanhm.ecommerce.be.model.Review;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.repository.ProductRepository;
import com.quanhm.ecommerce.be.repository.ReviewRepository;
import com.quanhm.ecommerce.be.request.ReviewRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImplement implements ReviewService {

    private ReviewRepository reviewRepository;
    private ProductService productService;
    private ProductRepository productRepository;

    public ReviewServiceImplement(ReviewRepository reviewRepository, ProductService productService, ProductRepository productRepository){
        this.reviewRepository = reviewRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Override
    public Review createReview(ReviewRequest req, User user) throws ProductExpection {
        Product product = productService.findProductById(req.getProductId());

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setReview(req.getReview());
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getAllReview(Long productId) {
        return reviewRepository.getAllProductsReview(productId);
    }
}
