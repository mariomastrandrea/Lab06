package it.polito.tdp.meteo.model;

import java.util.List;

public class Citta 
{	
	private final String nome;
	private final List<Rilevamento> rilevamenti;
	private int counter = 0;
	

	public Citta(String nome) 
	{
		this.nome = nome;
		this.rilevamenti = null;
	}
	
	public Citta(String nome, List<Rilevamento> rilevamenti) 
	{
		this.nome = nome;
		this.rilevamenti = rilevamenti;
	}

	public String getNome() 
	{
		return this.nome;
	}

	public List<Rilevamento> getRilevamenti() 
	{
		return this.rilevamenti;
	}

	public int getCounter() 
	{
		return this.counter;
	}

	public void setCounter(int counter) 
	{
		this.counter = counter;
	}
	
	public void increaseCounter() 
	{
		this.counter += 1;
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Citta other = (Citta) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	@Override
	public String toString() 
	{
		return this.nome;
	}
}
