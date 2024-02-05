package org.nicholas.spring.batchtask.config.processors;

import org.nicholas.spring.batchtask.model.Direction;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashSet;

public class DirectionProcessor implements ItemProcessor<Direction, Direction> {
    private HashSet<Direction> directionsSet;

    public DirectionProcessor(){
        directionsSet = new HashSet<>();
    }

    @Override
    public synchronized Direction process(Direction item) throws Exception {
        if (!directionsSet.contains(item)) {
            directionsSet.add(item);
            return item;
        }
        return null;
    }
}
