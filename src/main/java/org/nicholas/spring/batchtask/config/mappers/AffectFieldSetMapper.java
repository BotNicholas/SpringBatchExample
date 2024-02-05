package org.nicholas.spring.batchtask.config.mappers;

import org.nicholas.spring.batchtask.model.Affect;
import org.nicholas.spring.batchtask.model.Country;
import org.nicholas.spring.batchtask.model.Direction;
import org.nicholas.spring.batchtask.repo.CountryRepo;
import org.nicholas.spring.batchtask.repo.DirectionRepo;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class AffectFieldSetMapper implements FieldSetMapper<Affect> {
    private CountryRepo countryRepo;
    private DirectionRepo directionRepo;
    private HashMap<String, Country> countries;
    private HashMap<String, Direction> directions;

    public AffectFieldSetMapper(CountryRepo countryRepo, DirectionRepo directionRepo) {
        this.countryRepo = countryRepo;
        this.directionRepo = directionRepo;
        countries = new HashMap<>();
        directions = new HashMap<>();
    }

    private void initMaps(){
        if (countries.isEmpty() && directions.isEmpty()) {
            List<Country> countriesList = countryRepo.findAll();
            List<Direction> directionsList = directionRepo.findAll();

            countriesList.forEach(country -> countries.put(country.getCountryName(), country));
            directionsList.forEach(direction -> directions.put(direction.getDirectionName(), direction));
        }
    }

    @Override
    public Affect mapFieldSet(FieldSet fieldSet) throws BindException {
        initMaps();

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Affect affect = new Affect();
        try {
            affect.setDirection(directions.get(fieldSet.readString("Direction")));
            affect.setYear(fieldSet.readInt("Year"));

            Date date = format.parse(fieldSet.readString("Date"));
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            affect.setDate(localDate);

            affect.setWeekday(fieldSet.readString("Weekday"));
            affect.setCountry(countries.get(fieldSet.readString("Country")));
            affect.setCommodity(fieldSet.readString("Commodity"));
            affect.setTransportMode(fieldSet.readString("Transport_Mode"));
            affect.setMeasure(fieldSet.readString("Measure"));
            affect.setValue(fieldSet.readLong("Value"));
            affect.setCumulative(fieldSet.readLong("Cumulative"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return affect;
    }
}
