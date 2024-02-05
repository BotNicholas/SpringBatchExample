package org.nicholas.spring.batchtask.config.processors;

import org.nicholas.spring.batchtask.model.Affect;
import org.springframework.batch.item.ItemProcessor;

public class AffectItemProcessor implements ItemProcessor<Affect, Affect> {
    @Override
    public Affect process(Affect item) throws Exception {
//        System.out.println("\tprocessed");
        return item;
    }
}
