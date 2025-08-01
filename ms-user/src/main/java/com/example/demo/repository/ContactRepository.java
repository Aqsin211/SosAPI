package com.example.demo.repository;

import com.example.demo.entity.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<ContactEntity, Long> {
    boolean existsByName(String name);

    boolean existsByGmail(String gmail);

    boolean existsByPhoneNumber(String phoneNumber);
}
