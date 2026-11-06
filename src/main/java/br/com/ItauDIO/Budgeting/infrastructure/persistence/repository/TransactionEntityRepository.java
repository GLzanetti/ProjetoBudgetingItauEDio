package br.com.ItauDIO.Budgeting.infrastructure.persistence.repository;

import br.com.ItauDIO.Budgeting.domain.Category;
import br.com.ItauDIO.Budgeting.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransactionEntityRepository extends CrudRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByCategory(Category category);

    @Query("select sum(t.amount) from transaction_entity t where t.category = :category")
    Long sumAmountByCategory(@Param("category") Category category);
}
