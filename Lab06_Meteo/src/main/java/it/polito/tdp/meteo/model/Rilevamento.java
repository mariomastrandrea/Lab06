package it.polito.tdp.meteo.model;

import java.time.LocalDate;

public class Rilevamento 
{
	private final Citta localita;
	private final LocalDate data;
	private final int umidita;

	
	public Rilevamento(Citta localita, LocalDate data, int umidita) 
	{
		this.localita = localita;
		this.data = data;
		this.umidita = umidita;
	}

	public Citta getCitta() 
	{
		return this.localita;
	}

	public LocalDate getData() 
	{
		return this.data;
	}

	public int getUmidita() 
	{
		return this.umidita;
	}
	
	public int getCosto()
	{
		return this.umidita;
	}
}
