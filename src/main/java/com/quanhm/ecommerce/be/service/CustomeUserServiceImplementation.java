package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomeUserServiceImplementation implements UserDetailsService {

    private UserRepository userRepository;

    public CustomeUserServiceImplementation(UserRepository userRepository){
        this.userRepository = userRepository;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("Không tìm thấy người dùng có email này"+username);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassWord(), authorities);
    }
}
