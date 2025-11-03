package com.tecnocampus.LS2.protube_back.repositories;

import com.tecnocampus.LS2.protube_back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Long> {
}
