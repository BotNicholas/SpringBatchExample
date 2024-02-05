package org.nicholas.spring.batchtask.config.processors;

import org.nicholas.spring.batchtask.model.Affect;
import org.springframework.batch.item.ItemProcessor;

public class ExportAffectsItemProcessor implements ItemProcessor<Affect, Affect> {
    @Override
    public Affect process(Affect item) throws Exception {
//        StringBuilder builder = new StringBuilder();
//        builder.append(item.getDirection().getDirectionName()).append(",");
//        builder.append(item.getYear()).append(",");
//        builder.append(item.getDate()).append(",");
//        builder.append(item.getWeekday()).append(",");
//        builder.append(item.getCountry().getCountryName()).append(",");
//        builder.append(item.getCommodity()).append(",");
//        builder.append(item.getTransportMode()).append(",");
//        builder.append(item.getMeasure()).append(",");
//        builder.append(item.getValue()).append(",");
//        builder.append(item.getCumulative());
//        return builder.toString();
        return item;
    }
}
