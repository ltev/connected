package com.ltev.connected.repository;

import com.ltev.connected.domain.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

    Optional<UserDetails> findByUserUsername(String username);

    List<UserDetails> findByFirstName(String firstName);

    List<UserDetails> findByLastName(String lastName);

    List<UserDetails> findByFirstNameAndLastName(String firstName, String lastName);

    List<UserDetails> findByBirthdayBetween(LocalDate fromDate, LocalDate toDate);

    List<UserDetails> findByFirstNameAndBirthdayBetween(String firstName, LocalDate fromDate, LocalDate toDate);

    List<UserDetails> findByLastNameAndBirthdayBetween(String lastName, LocalDate fromDate, LocalDate toDate);

    List<UserDetails> findByFirstNameAndLastNameAndBirthdayBetween(
            String firstName, String lastName, LocalDate fromDate, LocalDate toDate);
}