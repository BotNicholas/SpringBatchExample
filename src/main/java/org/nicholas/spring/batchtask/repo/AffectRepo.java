package org.nicholas.spring.batchtask.repo;

import org.nicholas.spring.batchtask.model.Affect;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AffectRepo extends JpaRepository<Affect, Integer> {
}
