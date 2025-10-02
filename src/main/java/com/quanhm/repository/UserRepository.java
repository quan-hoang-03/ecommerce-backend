package com.quanhm.repository;

import com.quanhm.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);
}
