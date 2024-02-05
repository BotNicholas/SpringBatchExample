package org.nicholas.spring.batchtask.repo;

import org.nicholas.spring.batchtask.model.Country;
import org.nicholas.spring.batchtask.model.Direction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DirectionRepo extends JpaRepository<Direction, Integer> {
    public Direction findDirectionByDirectionName(String name);
}
