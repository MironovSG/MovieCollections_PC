package ru.mironov.moviecollections.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mironov.moviecollections.entity.User;
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}