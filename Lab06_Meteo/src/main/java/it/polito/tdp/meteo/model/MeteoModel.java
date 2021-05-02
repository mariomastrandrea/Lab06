package it.polito.tdp.meteo.model;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class MeteoModel 
{
	private final static int JOURNEY_COST = 100;
	private final static int MIN_CONSECUTIVE_DAYS_IN_THE_SAME_CITY = 3;
	private final static int MAX_DAYS_IN_THE_SAME_CITY = 6;
	private final static int NUM_OF_TOTAL_DAYS = 15;
	
	private final MeteoDAO meteoDao;
	private final Map<YearMonth, Map<Citta, Double>> avgHumiditiesByMonth;
	private final Map<LocalDate, Set<Rilevamento>> rilevamentiByDateIdMap;
	private final Map<String, Citta> cittaIdMap;
	
	
	public MeteoModel() 
	{
		this.meteoDao = new MeteoDAO();
		this.avgHumiditiesByMonth = new HashMap<>();
		this.rilevamentiByDateIdMap = new HashMap<>();
		this.cittaIdMap = new HashMap<>();
	}
	
	public List<Month> getAllMonths()
	{	
		return List.of(Month.values());
	}
	
	public List<Year> getAllYears()
	{
		return this.meteoDao.getAllYears();
	}

	public Map<Citta, Double> getUmiditaMedia(YearMonth selectedYearMonth) 
	{
		Map<Citta, Double> avgHumidityMap;
		
		if(!this.avgHumiditiesByMonth.containsKey(selectedYearMonth))
		{
			avgHumidityMap = this.meteoDao.getAvgHumidityOf(selectedYearMonth, this.cittaIdMap);
			this.avgHumiditiesByMonth.put(selectedYearMonth, avgHumidityMap);
		}
		else 
			avgHumidityMap = this.avgHumiditiesByMonth.get(selectedYearMonth);
		
		return avgHumidityMap;
	}

	public Solution computeOptimalSequencesFor(LocalDate startDate, LocalDate endDate)
	{		
		List<Set<Rilevamento>> rilevamentiByDate = this.getRilevamenti1(startDate, endDate);
		
		Set<Citta> citiesToBeVisited = this.getAllCitta(rilevamentiByDate);
		
		Set<List<Citta>> optimalSequences = new HashSet<>();
		List<Citta> partialSequence = new ArrayList<>();
		int actualCost = 0;
		IntObj minCost = IntObj.of(Integer.MAX_VALUE);
		
		this.recursiveComputation(partialSequence, actualCost, optimalSequences, minCost, rilevamentiByDate, citiesToBeVisited);
		
		Solution newSolution = new Solution(optimalSequences, minCost.value);
		return newSolution;
	}
	
	// level -> next position to check in 'rilevamentyByDate'; and next city to add
	
	private void recursiveComputation(List<Citta> partialSequence, int actualCost, 
								Set<List<Citta>> optimalSequences, IntObj minCost,
								List<Set<Rilevamento>> rilevamentiByDate, Set<Citta> allCitta)
	{
		int numTotDays = rilevamentiByDate.size();
		int level = partialSequence.size();
		
		if(level == numTotDays) //terminal case
		{	
			boolean citiesAllVisited = partialSequence.containsAll(allCitta);
			
			if(!citiesAllVisited)
				return; // * not admissible sequence *
						
			if(actualCost < minCost.value)
			{
				minCost.value = actualCost;
				optimalSequences.clear();
			}
			
			List<Citta> newOptimalSequence = new ArrayList<>(partialSequence);
			optimalSequences.add(newOptimalSequence);
			
			return;
		}
		
		//level < numTotDays
		
		Set<Rilevamento> rilevamentiOnDate = rilevamentiByDate.get(level);
		
		boolean canChangeCity = this.canChangeCity(partialSequence);
		
		if(!canChangeCity)
		{
			Citta lastCity = partialSequence.get(partialSequence.size() - 1);
			Rilevamento r = this.getRilevamento(rilevamentiOnDate, x -> x.getCitta().equals(lastCity));
			
			if(r == null)
				return; // * not admissible sequence * -> there is any rilevamento in this city
			
			//adding THE SAME city to partialSequence
			
			this.addingCityRecursiveComputation(r, partialSequence, actualCost, 
					optimalSequences, minCost, rilevamentiByDate, allCitta);
		}
		else
		{
			for(Rilevamento r : rilevamentiOnDate)
			{	
				//adding the city to partialSequence
				
				this.addingCityRecursiveComputation(r, partialSequence, actualCost, 
						optimalSequences, minCost, rilevamentiByDate, allCitta);
			}
		}
	}
	
	private void addingCityRecursiveComputation(Rilevamento r, List<Citta> partialSequence, int actualCost, 
									 Set<List<Citta>> optimalSequences, IntObj minCost, 
									 List<Set<Rilevamento>> rilevamentiByDate, Set<Citta> allCitta)
	{
		int level = partialSequence.size();
		Citta cityToBeAdded = r.getCitta();
		
		cityToBeAdded.increaseCounter(); // +1 stay
		if(cityToBeAdded.exceededDays(MAX_DAYS_IN_THE_SAME_CITY))
		{
			cityToBeAdded.decreaseCounter();
			return;		// * not admissible sequence * -> too many stays
		}
		
		int newActualCost = actualCost + r.getCosto();
		
		if(this.isChangingCity(partialSequence, cityToBeAdded))
			newActualCost += JOURNEY_COST;
		
		if(newActualCost > minCost.value)
		{
			cityToBeAdded.decreaseCounter();
			return;		// * not optimal sequence * --> too high cost
		}
		
		partialSequence.add(cityToBeAdded);
		
		this.recursiveComputation(partialSequence, newActualCost, optimalSequences, minCost, rilevamentiByDate, allCitta);
		
		//backtracking
		partialSequence.remove(level);
		cityToBeAdded.decreaseCounter();
	}
	
	private boolean isChangingCity(List<Citta> partialSequence, Citta city)
	{
		if(partialSequence.isEmpty())
			return false;
		
		Citta c = partialSequence.get(partialSequence.size() - 1);
		
		return !city.equals(c);
	}

	private Rilevamento getRilevamento(Set<Rilevamento> rilevamenti, Predicate<? super Rilevamento> predicate)
	{
		for(Rilevamento r : rilevamenti)
			if(predicate.test(r))
				return r;
		
		return null;
	}

	private boolean canChangeCity(List<Citta> partialSequence)
	{
		if(partialSequence.isEmpty())
			return true;
		
		boolean canBeChanged = false;
		Citta lastCity = null;
		int countSameCity = 0;
		
		for(int i=(partialSequence.size() - 1); i >= 0; i--)
		{
			Citta c = partialSequence.get(i);
			
			if(lastCity != null && !lastCity.equals(c))
				break;
			
			countSameCity++;
			
			if(lastCity == null)
			{
				lastCity = c;
			}
			else if(countSameCity == MIN_CONSECUTIVE_DAYS_IN_THE_SAME_CITY)
			{
				canBeChanged = true;
				break;
			}	
		}
		
		return canBeChanged;
	}
	
	//this is the most efficient
	private List<Set<Rilevamento>> getRilevamenti1(LocalDate startDate, LocalDate endDate)
	{
		return this.meteoDao.getRilevamenti(startDate, endDate, this.rilevamentiByDateIdMap, this.cittaIdMap);
	}
	
	//this is less efficient, because it does more connections to the db
	@SuppressWarnings("unused")
	private List<Set<Rilevamento>> getRilevamenti2(LocalDate startDate, LocalDate endDate)
	{
		List<Set<Rilevamento>> rilevamentiList = new ArrayList<>();
		LocalDate temp = startDate;
		
		while(temp.compareTo(endDate) <= 0)
		{
			Set<Rilevamento> rilevamenti;
			if(!this.rilevamentiByDateIdMap.containsKey(temp))
			{
				rilevamenti = this.meteoDao.getRilevamentiOn(temp, this.cittaIdMap);
				this.rilevamentiByDateIdMap.put(temp, rilevamenti);
			}
			else
				rilevamenti = this.rilevamentiByDateIdMap.get(temp);
			
			rilevamentiList.add(rilevamenti);
			
			temp = temp.plusDays(1);
		}
		
		return rilevamentiList;
	}

	public List<Integer> getNumGiorniPossibili()
	{
		return this.getListaFinoA(NUM_OF_TOTAL_DAYS*2);
	}
	
	public List<Integer> getListaFinoA(int bound)
	{
		List<Integer> list = new ArrayList<>();
		
		for(int i=1; i<=bound; i++)
		{
			list.add(i);
		}
		
		return list;
	}
	
	private Set<Citta> getAllCitta(List<Set<Rilevamento>> list)
	{
		Set<Citta> allCitta = new HashSet<>();
		boolean allFound = false;
		
		for(Set<Rilevamento> set : list)
		{
			for(Rilevamento r : set)
			{
				Citta c = r.getCitta();
				if(!allCitta.contains(c))
					allCitta.add(c);
				
				if(allCitta.size() == this.cittaIdMap.size())
				{
					allFound = true;
					break;
				}
			}
			if(allFound)
				break;
		}
		
		return allCitta;
	}
}
