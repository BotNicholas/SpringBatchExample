package org.nicholas.spring.batchtask.repo;

import org.nicholas.spring.batchtask.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepo extends JpaRepository<Country, Integer> {
    public Country findCountryByCountryName(String name);
}
