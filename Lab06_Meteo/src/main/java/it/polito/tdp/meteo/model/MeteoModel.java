package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import it.polito.tdp.meteo.DAO.MeteoDAO;

@SuppressWarnings("unused")
public class MeteoModel 
{
	private final static int JOURNEY_COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private final MeteoDAO meteoDao;
	private Map<Month, Map<Citta, Double>> avgHumiditiesByMonth;
	
	
	public MeteoModel() 
	{
		this.meteoDao = new MeteoDAO();
		this.avgHumiditiesByMonth = new HashMap<>();
	}
	
	public List<Month> getAllMonths()
	{
		return this.meteoDao.getAllMonths();
	}

	public Map<Citta, Double> getUmiditaMedia(Month selectedMonth) 
	{
		Map<Citta, Double> avgHumidityMap;
		
		if(!this.avgHumiditiesByMonth.containsKey(selectedMonth))
		{
			avgHumidityMap = this.meteoDao.getAvgHumidityOf(selectedMonth);
			this.avgHumiditiesByMonth.put(selectedMonth, avgHumidityMap);
		}
		else 
			avgHumidityMap = this.avgHumiditiesByMonth.get(selectedMonth);
		
		return avgHumidityMap;
	}
	
	public String trovaSequenza(int mese) 
	{
		//TODO: implement method
		return null;
	}
	

}
