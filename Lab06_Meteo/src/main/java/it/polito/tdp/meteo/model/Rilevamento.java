package it.polito.tdp.meteo.model;

import java.time.LocalDate;

public class Rilevamento 
{
	private final String localita;
	private final LocalDate data;
	private final int umidita;

	
	public Rilevamento(String localita, LocalDate data, int umidita) 
	{
		this.localita = localita;
		this.data = data;
		this.umidita = umidita;
	}

	public String getLocalita() 
	{
		return this.localita;
	}

	public LocalDate getData() 
	{
		return this.data;
	}

	public int getUmidita() 
	{
		return umidita;
	}
}
