package com.totvs.calcEmprestimos.domain.repositories;

import com.totvs.calcEmprestimos.domain.entities.Loan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository
{
	Optional<Loan> findById(UUID id);

	List<Loan> findAll();

	Loan save(Loan loan);

	void deleteById(UUID id);
}
