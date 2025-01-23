package com.ltev.connected.repository.userData;

import com.ltev.connected.domain.UserDetailsView;
import com.ltev.connected.repository.BaseViewRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.LocalDate;
import java.util.List;

@NoRepositoryBean
public interface UserDetailsViewRepository extends BaseViewRepository<UserDetailsView, Long> {

    List<UserDetailsView> findByFirstName(String firstName);

    List<UserDetailsView> findByLastName(String lastName);

    List<UserDetailsView> findByFirstNameAndLastName(String firstName, String lastName);

    List<UserDetailsView> findByBirthdayBetween(LocalDate fromDate, LocalDate toDate);

    List<UserDetailsView> findByFirstNameAndBirthdayBetween(String firstName, LocalDate fromDate, LocalDate toDate);

    List<UserDetailsView> findByLastNameAndBirthdayBetween(String lastName, LocalDate fromDate, LocalDate toDate);

    List<UserDetailsView> findByFirstNameAndLastNameAndBirthdayBetween(
            String firstName, String lastName, LocalDate fromDate, LocalDate toDate);
}