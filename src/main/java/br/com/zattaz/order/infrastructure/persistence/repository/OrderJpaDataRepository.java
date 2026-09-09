package br.com.zattaz.order.infrastructure.persistence.repository;

import br.com.zattaz.order.infrastructure.persistence.model.OrderEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderJpaDataRepository
        extends JpaRepository<OrderEntity, UUID>, JpaSpecificationExecutor<OrderEntity> {

    Optional<OrderEntity> findByIdAndDeletedFalse(UUID id);

    boolean existsByIdAndDeletedFalse(UUID id);
}
