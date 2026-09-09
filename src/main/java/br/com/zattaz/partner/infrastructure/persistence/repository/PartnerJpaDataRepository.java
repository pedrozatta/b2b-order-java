package br.com.zattaz.partner.infrastructure.persistence.repository;

import br.com.zattaz.partner.infrastructure.persistence.model.PartnerEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerJpaDataRepository extends JpaRepository<PartnerEntity, UUID> {

    Optional<PartnerEntity> findByIdAndDeletedFalse(UUID id);

    Page<PartnerEntity> findByDeletedFalse(Pageable pageable);
}
