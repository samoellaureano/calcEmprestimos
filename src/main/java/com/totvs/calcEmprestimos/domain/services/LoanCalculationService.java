package com.totvs.calcEmprestimos.domain.services;

import com.totvs.calcEmprestimos.domain.entities.Loan;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class LoanCalculationService
{

	private static final int BASE_DIAS = 360;
	private int totalParcelas = 0;

	public List<LoanResponse> calculate(Loan loan)
	{
		List<LoanResponse> responses = new ArrayList<>();

		LocalDate configDataAtual = LocalDate.from(loan.getStartDate());
		LocalDate configDataFinal = LocalDate.from(loan.getEndDate());
		LocalDate configPrimeiroPagamento = loan.getFirstInstallmentDate().toLocalDate();
		BigDecimal configTaxaJuros = loan.getInterestRate();
		BigDecimal emprestimoValorEmprestimo = loan.getLoanValue();
		BigDecimal emprestimoSaldoDevedor;
		BigDecimal principalSaldo = loan.getLoanValue();
		BigDecimal jurosAcumulado = BigDecimal.ZERO;
		LocalDate dataAnterior = configDataAtual;
		BigDecimal principalSaldoAnterior = principalSaldo;
		BigDecimal jurosAcumuladoAnterior = jurosAcumulado;

		// Calcula o total de parcelas
		totalParcelas = (int) (configDataAtual.until(configDataFinal).toTotalMonths());
		int countParcelas = 0;

		while (countParcelas < totalParcelas)
		{
			//Calculos
			String consolidada =
				isParcela(configDataAtual, configPrimeiroPagamento) ? (++countParcelas + "/"
					+ totalParcelas) : "";
			BigDecimal principalAmortizacao =
				consolidada.isEmpty() ? new BigDecimal(0) : calcAmortizacao(emprestimoValorEmprestimo);

			BigDecimal jurosProvisao = calcJurosProvisao(configDataAtual, dataAnterior, principalSaldo,
				jurosAcumulado, configTaxaJuros);
			BigDecimal jurosPago =
				consolidada.isEmpty() ? new BigDecimal(0) : jurosAcumulado.add(jurosProvisao);
			BigDecimal parcelaTotal = principalAmortizacao.add(jurosPago);

			//Atribui o valores da parcela
			LoanResponse response = new LoanResponse();
			response.setDataCompetencia(configDataAtual.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			response.setValorEmprestimo(emprestimoValorEmprestimo);
			response.setConsolidada(consolidada);
			response.setAmortizacao(consolidada.isEmpty() ? new BigDecimal(0) : principalAmortizacao);
			if (responses.isEmpty())
			{
				response.setJurosProvisao(BigDecimal.ZERO);
				response.setJurosAcumulado(BigDecimal.ZERO);
				response.setJurosPago(BigDecimal.ZERO);
				response.setSaldo(principalSaldo);
			}
			else
			{
				jurosAcumulado = calcJurosAcumulado(jurosAcumulado, jurosProvisao, jurosPago).setScale(2,
					RoundingMode.HALF_UP);
				principalSaldo = principalSaldoAnterior.subtract(principalAmortizacao);

				response.setJurosProvisao(jurosProvisao.setScale(2, RoundingMode.HALF_UP));
				response.setJurosAcumulado(jurosAcumulado.setScale(2, RoundingMode.HALF_UP));
				response.setJurosPago(consolidada.isEmpty() ? new BigDecimal(0)
					: jurosAcumuladoAnterior.add(jurosProvisao).setScale(2, RoundingMode.HALF_UP));
				response.setSaldo(principalSaldo.setScale(2, RoundingMode.HALF_UP));
			}
			emprestimoSaldoDevedor = calcSaldoDevedor(principalSaldo, jurosAcumulado);
			response.setSaldoDevedor(emprestimoSaldoDevedor);
			response.setParcelaTotal(parcelaTotal.setScale(2, RoundingMode.HALF_UP));

			// Atualiza as variáveis anteriores
			dataAnterior = configDataAtual;
			jurosAcumuladoAnterior = jurosAcumulado;
			principalSaldoAnterior = principalSaldo;

			// Incrementa a data para o próximo pagamento
			if (responses.isEmpty())
			{
				configDataAtual = changeToLastDayOfMonth(configDataAtual);
			}
			else
			{
				if (isParcela(configDataAtual, configPrimeiroPagamento))
				{
					configDataAtual = changeToLastDayOfMonth(configDataAtual);
				}
				else
				{
					configDataAtual = incrementMonth(configPrimeiroPagamento, configDataAtual);
				}
			}

			responses.add(response);
		}

		return responses;
	}

	/**
	 * Calcula o saldo devedor, formula =G60+I60 Emprestimo(saldo devedor)
	 * G60 = Principal (saldo)
	 * F60 = Juros (acumulado)
	 */
	private BigDecimal calcSaldoDevedor(BigDecimal saldoDevedor, BigDecimal acumulado)
	{
		return saldoDevedor.add(acumulado).setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * Calcula a parcela total, formula =I59+H60-J60 Juros(acumulado)
	 * I59 = Juros (acumulado)
	 * H60 = Juros (provisão)
	 * J60 = Juros (pago)
	 */
	private BigDecimal calcJurosAcumulado(BigDecimal jurosAcumuladoAnterior, BigDecimal jurosProvisao,
		BigDecimal jurosPago)
	{
		return jurosAcumuladoAnterior.add(jurosProvisao).subtract(jurosPago)
			.setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * Calcula a amortização, formula = =IF(D60<>"";$D$2/$G$2;0)
	 * D2 = Emprestimo (valor do empréstimo)
	 * G2 = Fixo (Quantidade de parcelas)
	 */
	private BigDecimal calcAmortizacao(BigDecimal valorEmprestimo)
	{
		// divide o valor do empréstimo pelo total de parcelas
		return valorEmprestimo.divide(BigDecimal.valueOf(totalParcelas), MathContext.DECIMAL64)
			.setScale(2, RoundingMode.HALF_UP);
	}

	private boolean isParcela(LocalDate dataAtual, LocalDate configPrimeiroPagamento)
	{
		return dataAtual.getDayOfMonth() == configPrimeiroPagamento.getDayOfMonth() && (
			dataAtual.isAfter(configPrimeiroPagamento) || dataAtual.isEqual(configPrimeiroPagamento));
	}

	/**
	 * Calcula o juros provisão, formula =((($E$2+1)^((A60-A59)/$F$2))-1)*(G59+I59)
	 * E2 = Inserido (taxa de juros)
	 * A8 = data competencia - atual
	 * A7 = data competencia - anterior
	 * F2 = Fixo (Base de dias)
	 * G7 = Principal (Saldo) - anterior
	 * I7 = Juros (acumulado)
	 */
	private BigDecimal calcJurosProvisao(LocalDate dataAtual, LocalDate dataAnterior,
		BigDecimal principalSaldo, BigDecimal jurosAcumulado, BigDecimal taxaJuros)
	{
		BigDecimal taxa = taxaJuros.divide(BigDecimal.valueOf(100)).add(BigDecimal.ONE);
		// diferença de dias entre a data atual e a data anterior
		double dias = ChronoUnit.DAYS.between(dataAnterior, dataAtual);
		// divide a diferença de dias pelo base de dias (360) com virgula
		dias = dias / BASE_DIAS;

		// Cálculo da potência
		double resultadoPotencia = Math.pow(taxa.doubleValue(), dias);
		// Subtração de 1 para obter o resultado final
		BigDecimal resultado = BigDecimal.valueOf(resultadoPotencia).subtract(BigDecimal.ONE);
		// saldo devedor + juros acumulado
		BigDecimal principalSaldoAtualizado = principalSaldo.add(jurosAcumulado);
		// multiplica o resultado da potenciação pelo saldo devedor + juros acumulado
		return BigDecimal.valueOf(resultado.doubleValue()).multiply(principalSaldoAtualizado);
	}

	private LocalDate changeToLastDayOfMonth(LocalDate current)
	{
		return current.withDayOfMonth(current.lengthOfMonth());
	}

	private LocalDate incrementMonth(LocalDate primeiroPagamento, LocalDate current)
	{
		// Incrementa um mês
		LocalDate newDate = current.plusMonths(1);
		// Altera o dia para o mesmo dia do primeiro pagamento
		if (current.getDayOfMonth() != primeiroPagamento.getDayOfMonth())
		{
			newDate = newDate.withDayOfMonth(primeiroPagamento.getDayOfMonth());
		}
		return newDate;
	}
}
