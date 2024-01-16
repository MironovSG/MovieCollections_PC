package ru.mironov.moviecollections.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mironov.moviecollections.entity.*;

import java.util.List;

@Repository
public interface MovieDetailsRepository extends JpaRepository<MovieDetails, Long> {
    List<MovieDetails> findAllByFormat(Format format);
    List<MovieDetails> findAllByPublisher(Publisher publisher);
    List<MovieDetails> findAllByCountry(Country country);

}
