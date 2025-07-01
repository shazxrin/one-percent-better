package io.github.shazxrin.onepercentbetter.checkin.repository;


import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;
import java.time.LocalDate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInRepository extends CrudRepository<CheckIn, Long> {
    CheckIn findByDate(LocalDate date);
}
