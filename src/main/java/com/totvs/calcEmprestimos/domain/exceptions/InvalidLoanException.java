package com.totvs.calcEmprestimos.domain.exceptions;

public class InvalidLoanException extends RuntimeException
{
	public InvalidLoanException(String message)
	{
		super(message);
	}
}
