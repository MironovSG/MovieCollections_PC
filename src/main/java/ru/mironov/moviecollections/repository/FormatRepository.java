package ru.mironov.moviecollections.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mironov.moviecollections.entity.Format;

@Repository
public interface FormatRepository extends JpaRepository<Format, Long> {
    Format findByName(String name);
}