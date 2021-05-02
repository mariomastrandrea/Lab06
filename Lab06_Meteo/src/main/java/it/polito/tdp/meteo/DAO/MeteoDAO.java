package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO 
{	
	public List<Year> getAllYears()
	{
		final String sqlQuery = String.format("%s %s %s",
									"SELECT DISTINCT YEAR(Data) AS anno",
									"FROM situazione",
									"ORDER BY anno ASC");
		
		final List<Year> years = new ArrayList<>();
		
		try
		{
			Connection connection = ConnectDB.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			ResultSet result = statement.executeQuery();
			
			while(result.next())
			{
				Year y = Year.of(result.getInt("anno"));
				years.add(y);
			}
			
			ConnectDB.close(result, statement, connection);
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
			throw new RuntimeException("Error in getAllYears()", sqle);
		}
		
		return years;
	}
	
	public Map<Citta, Double> getAvgHumidityOf(YearMonth yearMonth, Map<String,Citta> idMap)
	{
		final String sqlQuery = String.format("%s %s %s %s",
									"SELECT Localita, AVG(umidita) AS mediaUmidita",
									"FROM situazione",
									"WHERE YEAR(Data) = ? AND MONTH(Data) = ?",
									"GROUP BY Localita");
		
		final Map<Citta, Double> humidityByCitta = new HashMap<>();
		
		try
		{
			Connection connection = ConnectDB.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, yearMonth.getYear());
			statement.setInt(2, yearMonth.getMonthValue());
			ResultSet result = statement.executeQuery();
			
			while(result.next())
			{
				String cityName = result.getString("Localita");
				
				Citta city;
				if(idMap.containsKey(cityName))
					city = idMap.get(cityName);
				else 
				{
					city = new Citta(cityName);
					idMap.put(cityName, city);
				}
				
				Double avgHumidity = result.getDouble("mediaUmidita");
				humidityByCitta.put(city, avgHumidity);
			}
			
			ConnectDB.close(result, statement, connection);
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
			throw new RuntimeException("Error in getAvgHumidityOf()", sqle);
		}
		
		return humidityByCitta;
	}

	public List<Set<Rilevamento>> getRilevamenti(LocalDate startDate, LocalDate endDate,
			Map<LocalDate, Set<Rilevamento>> rilevamentiByDateIdMap, Map<String, Citta> cittaIdMap)
	{
		String sqlQuery = String.format("%s %s %s %s %s",
				 						"SELECT Data, Localita, Umidita",
				 						"FROM situazione",
				 						"WHERE Data >= ? AND Data <= ?",
				 						"GROUP BY Data, Localita, Umidita",
				 						"ORDER BY Data ASC");
		 
		List<Set<Rilevamento>> rilevamentiList = new ArrayList<>();
		 
		try
		{
			Connection connection = ConnectDB.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setDate(1, Date.valueOf(startDate));
			statement.setDate(2, Date.valueOf(endDate));
			ResultSet result = statement.executeQuery();
			
			LocalDate actualDate = null;
			Set<Rilevamento> actualRilevamenti = null;
			boolean alreadyCreated = false;
			
			while(result.next())
			{
				LocalDate temp = result.getDate("Data").toLocalDate();
				
				if(actualDate == null || !temp.equals(actualDate))
				{
					//date has changed
					actualDate = temp;
					alreadyCreated = rilevamentiByDateIdMap.containsKey(actualDate);
					
					if(alreadyCreated)
						actualRilevamenti = rilevamentiByDateIdMap.get(actualDate);
					else
					{
						actualRilevamenti = new HashSet<>();
						rilevamentiByDateIdMap.put(actualDate, actualRilevamenti);
					}
					
					rilevamentiList.add(actualRilevamenti);
				}
				
				if(!alreadyCreated)
				{	
					String cityName = result.getString("Localita");
					
					Citta city;
					if(cittaIdMap.containsKey(cityName))
						city = cittaIdMap.get(cityName);
					else
					{
						city = new Citta(cityName);
						cittaIdMap.put(cityName, city);
					}
					
					Rilevamento newRilevamento = new Rilevamento(city, actualDate, result.getInt("Umidita"));
					actualRilevamenti.add(newRilevamento);
				}
			}
			
			ConnectDB.close(result, statement, connection);			
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
			throw new RuntimeException("Error in getRilevamenti()", sqle);
		}
		
		return rilevamentiList;
	}
	
	public Set<Rilevamento> getRilevamentiOn(LocalDate date, Map<String, Citta> cittaIdMap)
	{
		String sqlQuery = String.format("%s %s %s",
				 						"SELECT Localita, Umidita",
				 						"FROM situazione",
				 						"WHERE Data = ?");
		 
		Set<Rilevamento> rilevamenti = new HashSet<>();
		 
		try
		{
			Connection connection = ConnectDB.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setDate(1, Date.valueOf(date));
			ResultSet result = statement.executeQuery();
			
			while(result.next())
			{
				String cityName = result.getString("Localita");
					
				Citta city;
				if(cittaIdMap.containsKey(cityName))
					city = cittaIdMap.get(cityName);
				else
				{
					city = new Citta(cityName);
					cittaIdMap.put(cityName, city);
				}
					
				Rilevamento newRilevamento = new Rilevamento(city, date, result.getInt("Umidita"));
				rilevamenti.add(newRilevamento);
			}
			
			ConnectDB.close(result, statement, connection);			
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
			throw new RuntimeException("Error in getRilevamentiOn()", sqle);
		}
		
		return rilevamenti;
	}
}
