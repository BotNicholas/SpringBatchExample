package org.nicholas.spring.batchtask.config.processors;

import org.nicholas.spring.batchtask.model.Country;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashSet;
import java.util.Set;

public class CountryProcessor implements ItemProcessor<Country, Country> {
    private Set<Country> countrySet;
    public CountryProcessor(){
        countrySet = new HashSet<>();
    }

    @Override
    public synchronized Country process(Country item) throws Exception {
        if (!countrySet.contains(item)) {
                countrySet.add(item);
            return item;
        }
        return null;
    }
}
