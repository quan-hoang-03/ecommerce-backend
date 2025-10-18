package com.quanhm.ecommerce.be.repository;

import com.quanhm.ecommerce.be.model.Cart;
import com.quanhm.ecommerce.be.model.CartItem;
import com.quanhm.ecommerce.be.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart = :cart AND ci.product = :product AND ci.size = :size AND ci.userId =:userId")
    public CartItem isCartItemExist(@Param("cart") Cart cart, @Param("product") Product product, @Param("size") String size, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.product.id = :productId")
    void deleteAllByProductId(@Param("productId") Long productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id IN (SELECT c.id FROM Cart c WHERE c.user.id = :userId)")
    void deleteByUserItemCartId(@Param("userId") Long userId);
}
