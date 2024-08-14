package com.totvs.calcEmprestimos.domain.controller;

import com.totvs.calcEmprestimos.domain.entities.Loan;
import com.totvs.calcEmprestimos.domain.services.LoanCalculationService;
import com.totvs.calcEmprestimos.domain.services.LoanRequestPayload;
import com.totvs.calcEmprestimos.domain.services.LoanResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calcLoan")
public class CalculationController
{
	private final LoanCalculationService loanCalculationService = new LoanCalculationService();

	@PostMapping("/calculate")
	public ResponseEntity<List<LoanResponse>> calcular(@RequestBody LoanRequestPayload payload)
	{
		Loan loan = new Loan(payload);
		List<LoanResponse> loanResponses = loanCalculationService.calculate(loan);

		return ResponseEntity.ok(loanResponses);
	}
}
