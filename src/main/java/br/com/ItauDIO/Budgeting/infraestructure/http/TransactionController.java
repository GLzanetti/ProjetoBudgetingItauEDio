package br.com.ItauDIO.Budgeting.infraestructure.http;

import br.com.ItauDIO.Budgeting.application.ListTransactionsByCategoryUseCase;
import br.com.ItauDIO.Budgeting.application.PersistTransactionUseCase;
import br.com.ItauDIO.Budgeting.domain.Category;
import br.com.ItauDIO.Budgeting.infraestructure.http.request.TransactionRequest;
import br.com.ItauDIO.Budgeting.infraestructure.http.response.TransactionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final PersistTransactionUseCase persistTransactionUseCase;
    private final ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;

    public TransactionController(PersistTransactionUseCase persistTransactionUseCase, ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase) {
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.listTransactionsByCategoryUseCase = listTransactionsByCategoryUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request){
        var transaction = persistTransactionUseCase.execute(request.toInput());
        return TransactionResponse.from(transaction);
    }

    @GetMapping("/{category}")
    public List<TransactionResponse> readTransactions(@PathVariable Category category){
        return listTransactionsByCategoryUseCase.execute(category).stream().map(TransactionResponse::from).toList();
    }
}
