package ru.mironov.moviecollections.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mironov.moviecollections.entity.Country;
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findByName(String name);
}

