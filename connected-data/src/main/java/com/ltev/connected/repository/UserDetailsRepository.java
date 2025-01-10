package com.ltev.connected.repository;

import com.ltev.connected.domain.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

    List<UserDetails> findByFirstName(String firstName);

    List<UserDetails> findByLastName(String lastName);

    List<UserDetails> findByFirstNameAndLastName(String firstName, String lastName);
}