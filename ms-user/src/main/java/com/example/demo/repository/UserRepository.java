package com.example.demo.repository;

import com.example.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByGmail(String gmail);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    UserEntity findByUsername(String username);

}
