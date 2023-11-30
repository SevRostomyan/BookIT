package com.bookit.bookit.repository.user;


import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    int countByRole(UserRole role);
    Optional<UserEntity> findUserByEmail(String email);

    // Add a method to search for users by username or email


    List<UserEntity> findByFirstnameContainingOrLastnameContainingOrEmailContainingAndRole(String firstname, String lastname, String email, UserRole role);


    List<UserEntity> findByRole(UserRole role);
}
