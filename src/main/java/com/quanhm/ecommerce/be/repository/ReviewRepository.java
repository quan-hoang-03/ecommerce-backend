package com.quanhm.ecommerce.be.repository;

import com.quanhm.ecommerce.be.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
