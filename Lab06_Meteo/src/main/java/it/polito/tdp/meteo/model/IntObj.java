package it.polito.tdp.meteo.model;

public class IntObj
{
	public int value;
	
	private IntObj(int i)
	{
		this.value = i;
	}
	
	public static IntObj of(int value)
	{
		return new IntObj(value);
	}
}
