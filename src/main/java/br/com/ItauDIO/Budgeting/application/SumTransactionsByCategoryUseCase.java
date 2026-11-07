package br.com.ItauDIO.Budgeting.application;

import br.com.ItauDIO.Budgeting.application.output.CategoryTotalOutput;
import br.com.ItauDIO.Budgeting.domain.Category;
import br.com.ItauDIO.Budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class SumTransactionsByCategoryUseCase {
    private final TransactionRepository transactionRepository;

    public SumTransactionsByCategoryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "Sum-transactions-by-category",
            description = "Calcula o valor total gasto em uma categoria, somando todas as transações dela. "
                    + "Use quando o usuário perguntar quanto gastou no total. Retorna o total em reais.")
    public CategoryTotalOutput execute(
            @ToolParam(description = "Categoria a somar: GROCERIES (mercado), PHARMA (farmácia) ou AUTO (carro)")
            Category category) {
        long total = transactionRepository.sumAmountByCategory(category);
        return CategoryTotalOutput.from(category, total);
    }
}
