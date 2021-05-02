package it.polito.tdp.meteo.model;

import java.time.YearMonth;

public class TestModel 
{
	public static void main(String[] args) 
	{
		for(int i=2010; i<=2020; i++)
		{
			YearMonth ym = YearMonth.of(i, 2);
			System.out.println(ym + " -> " + ym.lengthOfMonth() + " days");
		}
	}
}
