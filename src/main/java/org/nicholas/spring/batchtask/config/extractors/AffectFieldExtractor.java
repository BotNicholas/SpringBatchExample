package org.nicholas.spring.batchtask.config.extractors;

import org.nicholas.spring.batchtask.model.Affect;
import org.springframework.batch.item.file.transform.FieldExtractor;

public class AffectFieldExtractor implements FieldExtractor<Affect> {
    @Override
    public Object[] extract(Affect item) {
        return new Object[]{item.getDirection().getDirectionName(),
                item.getYear(),
                item.getDate(),
                item.getWeekday(),
                item.getCountry().getCountryName(),
                item.getCommodity(),
                item.getTransportMode(),
                item.getMeasure(),
                item.getValue().toString(),
                item.getCumulative().toString()
        };
    }
}
