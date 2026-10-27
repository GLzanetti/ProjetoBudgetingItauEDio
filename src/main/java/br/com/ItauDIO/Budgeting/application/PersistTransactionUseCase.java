package br.com.ItauDIO.Budgeting.application;

import br.com.ItauDIO.Budgeting.application.input.PersistTransactionInput;
import br.com.ItauDIO.Budgeting.application.output.TransactionOutput;
import br.com.ItauDIO.Budgeting.domain.Transaction;
import br.com.ItauDIO.Budgeting.domain.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class PersistTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public PersistTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionOutput execute(PersistTransactionInput input){
        var transaction = transactionRepository.save(new Transaction(input.description(), input.amount(), input.category()));
        return TransactionOutput.from(transaction);
    }
}
