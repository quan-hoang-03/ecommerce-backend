package com.quanhm.ecommerce.be.controller;

import com.quanhm.ecommerce.be.config.JwtProvide;
import com.quanhm.ecommerce.be.exception.UserException;
import com.quanhm.ecommerce.be.model.User;
import com.quanhm.ecommerce.be.repository.UserRepository;
import com.quanhm.ecommerce.be.request.LoginRequest;
import com.quanhm.ecommerce.be.response.AuthResponse;
import com.quanhm.ecommerce.be.service.CustomeUserServiceImplementation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserRepository userRepository;
    private JwtProvide jwtProvide;
    private PasswordEncoder passwordEncoder;
    private CustomeUserServiceImplementation customeUserService;

    public AuthController(UserRepository userRepository,CustomeUserServiceImplementation customeUserService,PasswordEncoder passwordEncoder, JwtProvide jwtProvide){
        this.userRepository = userRepository;
        this.customeUserService = customeUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvide = jwtProvide;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse>createUserHandle(@RequestBody User user) throws UserException{
        String email = user.getEmail();
        String passWord = user.getPassWord();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        User isEmailExist = userRepository.findByEmail(email);
        if(isEmailExist!=null){
            throw new UserException("Email đã tồn tại với tài khoản khác");
        }

        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setPassWord(passwordEncoder.encode(passWord));
        createdUser.setFirstName(firstName);
        createdUser.setLastName(lastName);

        User savedUser = userRepository.save(createdUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(),savedUser.getPassWord());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvide.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Đăng ký thành công");
        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse>loginUserHandle(@RequestBody LoginRequest loginRequest){
        String username = loginRequest.getEmail();
        String passWord = loginRequest.getPassword();
        Authentication authentication = authenticate(username,passWord);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvide.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Đăng nhập thành công");
        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);

    }

    private Authentication authenticate(String username, String passWord) {
        UserDetails userDetails = customeUserService.loadUserByUsername(username);
        if(userDetails == null){
            throw new BadCredentialsException("Tên người dùng không hợp lệ");
        }
        if(!passwordEncoder.matches(passWord,userDetails.getPassword())){
            throw new BadCredentialsException("Mật khẩu không hợp lệ");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }
}
