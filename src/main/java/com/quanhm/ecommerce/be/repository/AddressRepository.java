package com.quanhm.ecommerce.be.repository;

import com.quanhm.ecommerce.be.model.Address;
import com.quanhm.ecommerce.be.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository <Address, Long>{
    List<Address> findByUser(User user);
}
