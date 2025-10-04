package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.config.JwtProvide;
import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService{

    private UserRepository userRepository;
    private JwtProvide jwtProvide;

    public UserServiceImplementation(UserRepository userRepository,JwtProvide jwtProvide){
        this.userRepository = userRepository;
        this.jwtProvide = jwtProvide;
    }

    @Override
    public User findUserById(Long userId) throws UserException {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            return user.get();
        }
        throw new UserException("Không tìm thấy id người dùng"+userId);
    }

    @Override
    public User findUserProfileByJwt(String jwt) throws UserException {
        String email = jwtProvide.getEmailFromToken(jwt);
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UserException("Không tìm thấy gmail người dùng"+email);
        }
        return user;
    }
}
