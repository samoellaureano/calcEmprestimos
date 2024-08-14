package com.totvs.calcEmprestimos.domain.services;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanResponse
{
	private String dataCompetencia;
	private BigDecimal valorEmprestimo;
	private BigDecimal saldoDevedor;
	private String consolidada;
	private BigDecimal parcelaTotal;
	private BigDecimal amortizacao;
	private BigDecimal saldo;
	private BigDecimal jurosProvisao;
	private BigDecimal jurosAcumulado;
	private BigDecimal jurosPago;
}
