package br.com.zattaz.product.infrastructure.persistence.repository;

import br.com.zattaz.product.infrastructure.persistence.model.ProductEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaDataRepository extends JpaRepository<ProductEntity, UUID> {

    Optional<ProductEntity> findByIdAndDeletedFalse(UUID id);

    Page<ProductEntity> findByDeletedFalse(Pageable pageable);
}
