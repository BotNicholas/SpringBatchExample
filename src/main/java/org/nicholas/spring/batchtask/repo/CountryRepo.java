package org.nicholas.spring.batchtask.repo;

import org.nicholas.spring.batchtask.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepo extends JpaRepository<Country, Integer> {
    public Country findCountryByCountryName(String name);
}
