package az.company.mssos.repository;

import az.company.mssos.entity.SosAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SosAlertRepository extends JpaRepository<SosAlert, Long> {
}
