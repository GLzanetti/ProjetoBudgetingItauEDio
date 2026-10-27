package br.com.ItauDIO.Budgeting.application;

import br.com.ItauDIO.Budgeting.application.output.TransactionOutput;
import br.com.ItauDIO.Budgeting.domain.Category;
import br.com.ItauDIO.Budgeting.domain.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTransactionsByCategoryUseCase {
    private final TransactionRepository transactionRepository;

    public ListTransactionsByCategoryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionOutput> execute(Category category){
        return transactionRepository.findALlByCategory(category).stream().map(TransactionOutput::from).toList();
    }
}
