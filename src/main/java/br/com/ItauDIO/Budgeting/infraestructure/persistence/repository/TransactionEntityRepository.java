package br.com.ItauDIO.Budgeting.infraestructure.persistence.repository;

import br.com.ItauDIO.Budgeting.domain.Category;
import br.com.ItauDIO.Budgeting.infraestructure.persistence.entity.TransactionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionEntityRepository extends CrudRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByCategory(Category category);
}
