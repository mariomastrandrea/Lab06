package it.polito.tdp.meteo.model;

import java.util.List;
import java.util.Set;

public class Solution
{
	private final Set<List<Citta>> optimalSequences;
	private final int minCost;
	
	
	public Solution(Set<List<Citta>> optimalSequences, int minCost)
	{
		this.optimalSequences = optimalSequences;
		this.minCost = minCost;
	}

	public Set<List<Citta>> getOptimalSequences()
	{
		return this.optimalSequences;
	}

	public int getMinCost()
	{
		return this.minCost;
	}
}
