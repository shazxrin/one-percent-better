package io.github.shazxrin.onepercentbetter.repository;


import io.github.shazxrin.onepercentbetter.model.CheckIn;
import java.time.LocalDate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInRepository extends CrudRepository<CheckIn, String> {
    CheckIn findByDate(LocalDate date);
}
