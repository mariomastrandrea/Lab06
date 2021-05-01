package it.polito.tdp.meteo.model;

public enum Month
{
	JANUARY(1,"Gennaio"),
	FEBRUARY(2,"Febbraio"),
	MARCH(3,"Marzo"),
	APRIL(4,"Aprile"),
	MAY(5,"Maggio"),
	JUNE(6,"Giugno"),
	JULY(7,"Luglio"),
	AUGUST(8,"Agosto"),
	SEPTEMBER(9,"Settembre"),
	OCTOBER(10,"Ottobre"),
	NOVEMBER(11,"Novembre"),
	DECEMBER(12,"Dicembre");
	
	private int monthNumber;
	private String italianName;
	
	
	private Month(int num, String italianName)
	{
		this.monthNumber = num;
		this.italianName = italianName;
	}
	
	public int getNum() { return this.monthNumber; }
	
	@Override
	public String toString()
	{
		return this.italianName;
	}
	
	public static Month num(int num)
	{
		if(num < 1 || num > 12)
			throw new IllegalArgumentException("It does not exist a month number " + num + "!\nTry a number in 1-12");
		
		Month m =  Month.values()[num - 1];
		return m;
	}
}
