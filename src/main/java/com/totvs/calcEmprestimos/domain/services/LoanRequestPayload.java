package com.totvs.calcEmprestimos.domain.services;

import java.math.BigDecimal;

public record LoanRequestPayload(String dataInicial, String dataFinal, String primeiroPagamento,
                                 BigDecimal valorEmprestimo, Double taxaJuros)
{
}
