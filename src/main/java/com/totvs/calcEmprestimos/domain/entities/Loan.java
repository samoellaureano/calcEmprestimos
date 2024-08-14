package com.totvs.calcEmprestimos.domain.entities;

import com.totvs.calcEmprestimos.domain.services.LoanRequestPayload;
import com.totvs.calcEmprestimos.domain.valueObjects.Date;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;

	@Column(name = "first_installment_date", nullable = false)
	private LocalDateTime firstInstallmentDate;

	@Column(name = "loan_value", nullable = false)
	private BigDecimal loanValue;

	@Column(name = "interest_rate", nullable = false)
	private BigDecimal interestRate;

	public Loan(LoanRequestPayload data)
	{
		this.startDate = new Date(data.dataInicial()).getValue();
		this.endDate = new Date(data.dataFinal()).getValue();
		this.loanValue = new BigDecimal(data.valorEmprestimo().toString());
		this.interestRate = new BigDecimal(data.taxaJuros().toString());
		this.firstInstallmentDate = new Date(data.primeiroPagamento()).getValue();
		validate();
	}

	private void validate()
	{
		if (startDate == null)
		{
			throw new IllegalArgumentException("Data inicial não pode ser nula");
		}
		if (endDate == null)
		{
			throw new IllegalArgumentException("Data final não pode ser nula");
		}
		if (loanValue == null)
		{
			throw new IllegalArgumentException("Valor do empréstimo não pode ser nulo");
		}
		if (interestRate == null)
		{
			throw new IllegalArgumentException("Taxa de juros não pode ser nula");
		}
	}
}
