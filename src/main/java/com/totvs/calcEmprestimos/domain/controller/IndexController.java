package com.totvs.calcEmprestimos.domain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController
{
	@RequestMapping(value = "/")
	public String forward()
	{
		return "forward:/index.html";
	}
}