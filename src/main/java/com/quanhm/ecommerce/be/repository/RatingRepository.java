package com.quanhm.ecommerce.be.repository;

import com.quanhm.ecommerce.be.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
