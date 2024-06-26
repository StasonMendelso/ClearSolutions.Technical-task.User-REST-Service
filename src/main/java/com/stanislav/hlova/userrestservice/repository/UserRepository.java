package com.stanislav.hlova.userrestservice.repository;

import com.stanislav.hlova.userrestservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    List<User> findUsersByBirthdateGreaterThanEqualAndBirthdateLessThanEqual(LocalDate from, LocalDate to);
}
