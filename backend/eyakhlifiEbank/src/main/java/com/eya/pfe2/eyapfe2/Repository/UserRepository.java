package com.eya.pfe2.eyapfe2.Repository;

import com.eya.pfe2.eyapfe2.Models.Role;
import com.eya.pfe2.eyapfe2.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByTelephone(String telephone);
    Optional<User> findByRole(Role role);
}
