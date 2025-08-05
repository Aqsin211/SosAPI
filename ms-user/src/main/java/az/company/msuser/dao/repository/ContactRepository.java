package az.company.msuser.dao.repository;

import az.company.msuser.dao.entity.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<ContactEntity, Long> {
    boolean existsByName(String name);

    boolean existsByGmail(String gmail);

    boolean existsByPhoneNumber(String phoneNumber);
}
