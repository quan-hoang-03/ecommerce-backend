package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.User;

public interface UserService {
    public User findUserById(Long userId) throws UserException;

    public User findUserProfileByJwt(String jwt) throws UserException;
}
