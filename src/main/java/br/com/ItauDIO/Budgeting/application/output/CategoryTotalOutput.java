package br.com.ItauDIO.Budgeting.application.output;

import br.com.ItauDIO.Budgeting.domain.Category;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record CategoryTotalOutput(String category, BigDecimal total) {
    public static CategoryTotalOutput from(Category category, long totalInCents) {
        return new CategoryTotalOutput(
                category.name(),
                BigDecimal.valueOf(totalInCents, 2).setScale(2, RoundingMode.HALF_UP)
        );
    }
}
