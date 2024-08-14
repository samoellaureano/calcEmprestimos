package com.totvs.calcEmprestimos.domain.valueObjects;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Date
{
	private LocalDateTime value;

	public Date(String value)
	{
		this.value = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
	}

	public Date(LocalDateTime value)
	{
		this.value = value;
	}

	public boolean isBefore(LocalDateTime startDate)
	{
		return this.value.isBefore(startDate);
	}

	public boolean isAfter(LocalDateTime endDate)
	{
		return this.value.isAfter(endDate);
	}

	public LocalDateTime getValue()
	{
		return this.value;
	}
}
