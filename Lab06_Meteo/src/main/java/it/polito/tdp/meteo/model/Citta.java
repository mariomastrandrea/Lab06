package it.polito.tdp.meteo.model;

public class Citta 
{	
	private final String nome;
	private int counter = 0;
	

	public Citta(String nome) 
	{
		this.nome = nome;
	}

	public String getNome() 
	{
		return this.nome;
	}

	public int getCounter() 
	{
		return this.counter;
	}
	
	public void increaseCounter() 
	{
		this.counter += 1;
	}
	
	public void decreaseCounter()
	{
		this.counter -= 1;
	}
	
	public boolean exceededDays(int days)
	{
		return this.counter > days;
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
