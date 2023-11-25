package com.bookit.bookit.repository.admin;

import com.bookit.bookit.entity.admin.Admin;
import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    //Optional<Admin> findAdminByEmail(String email);

    List<UserEntity> findAllByRole(UserRole admin);
    // Check if an admin with the given user ID exists

}
