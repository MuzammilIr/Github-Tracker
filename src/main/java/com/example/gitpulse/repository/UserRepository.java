
package com.example.gitpulse.repository;

import com.example.gitpulse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
