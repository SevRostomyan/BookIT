package com.bookit.bookit.repository.user;

import com.bookit.bookit.entity.user.User;
import com.bookit.bookit.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    int countByRole(UserRole role);
    Optional<User> findUserByEmail(String email);
}
