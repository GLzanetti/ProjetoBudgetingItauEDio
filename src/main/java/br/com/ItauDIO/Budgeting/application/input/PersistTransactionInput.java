package br.com.ItauDIO.Budgeting.application.input;

import br.com.ItauDIO.Budgeting.domain.Category;
import org.springframework.ai.tool.annotation.ToolParam;

public record PersistTransactionInput(@ToolParam(description = "Descrição de uma transação")String description,
                                      @ToolParam(description = "Categoria de uma transação") Category category,
                                      @ToolParam(description = "Valor de uma transação(em centavos)") long amount) {
}
