package ru.mironov.moviecollections.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mironov.moviecollections.entity.Movie;

import java.util.List;
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByCreatedBy(Long createdBy);
}