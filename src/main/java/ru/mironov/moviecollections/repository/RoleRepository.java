package ru.mironov.moviecollections.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mironov.moviecollections.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}