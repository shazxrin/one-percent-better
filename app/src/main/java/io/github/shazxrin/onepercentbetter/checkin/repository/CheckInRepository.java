package io.github.shazxrin.onepercentbetter.checkin.repository;


import io.github.shazxrin.onepercentbetter.checkin.model.CheckIn;
import java.time.LocalDate;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInRepository extends ListCrudRepository<CheckIn, Long> {
    CheckIn findByDate(LocalDate date);
}
