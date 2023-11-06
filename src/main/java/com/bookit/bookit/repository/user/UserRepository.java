package com.bookit.bookit.repository.user;


import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    int countByRole(UserRole role);
    Optional<UserEntity> findUserByEmail(String email);
}
