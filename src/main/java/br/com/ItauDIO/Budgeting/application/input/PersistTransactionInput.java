package br.com.ItauDIO.Budgeting.application.input;

import br.com.ItauDIO.Budgeting.domain.Category;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * Parâmetros de entrada para o caso de uso de persistência de transações.
 *
 * @param description descrição do lançamento financeiro
 * @param category categoria atribuída ao gasto
 * @param amount valor da transação em centavos inteiros (ex: R$ 50,00 deve ser enviado como 5000)
 */
public record PersistTransactionInput(
        @ToolParam(description = "Descrição de uma transação") String description,
        @ToolParam(description = "Categoria de uma transação") Category category,
        @ToolParam(description = "Valor da transação obrigatoriamente em centavos inteiros (exemplo: 50 reais deve ser informado como 5000)") long amount
) {
}
