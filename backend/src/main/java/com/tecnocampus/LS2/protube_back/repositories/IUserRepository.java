package com.tecnocampus.LS2.protube_back.repositories;

import com.tecnocampus.LS2.protube_back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
