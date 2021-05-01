package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Month;
import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO 
{
	public List<Rilevamento> getAllRilevamenti() 
	{
		final String sql = String.format("%s %s %s",
										"SELECT Localita, Data, Umidita",
										"FROM situazione",
										"ORDER BY data ASC");

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try 
		{
			Connection connection = ConnectDB.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet result = statement.executeQuery();

			while (result.next()) 
			{
				Rilevamento r = new Rilevamento(result.getString("Localita"), 
												result.getDate("Data").toLocalDate(), 
												result.getInt("Umidita"));
				rilevamenti.add(r);
			}
			
			result.close();
			statement.close();
			connection.close();
		} 
		catch (SQLException sqle) 
		{

			sqle.printStackTrace();
			throw new RuntimeException(sqle);
		}
		return rilevamenti;
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) 
	{
		//TODO: implementare metodo DAO
		return null;
	}

	public List<Month> getAllMonths()
	{
		final String sqlQuery = String.format("%s %s %s",
									"SELECT DISTINCT MONTH(Data) AS mese",
									"FROM situazione",
									"ORDER BY mese ASC");
		
		final List<Month> months = new ArrayList<>();
		
		try
		{
			Connection connection = ConnectDB.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			ResultSet result = statement.executeQuery();
			
			while(result.next())
			{
				Month m = Month.num(result.getInt("mese"));
				months.add(m);
			}
			
			ConnectDB.close(result, statement, connection);
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
			throw new RuntimeException("Error in getAllMonths()", sqle);
		}
		
		return months;
	}
	
	public Map<Citta, Double> getAvgHumidityOf(Month month)
	{
		final String sqlQuery = String.format("%s %s %s %s",
									"SELECT Localita, AVG(umidita) AS mediaUmidita",
									"FROM situazione",
									"WHERE MONTH(Data) = ?",
									"GROUP BY Localita");
		
		final Map<Citta, Double> humidityByCitta = new HashMap<>();
		
		try
		{
			Connection connection = ConnectDB.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, month.getNum());
			ResultSet result = statement.executeQuery();
			
			while(result.next())
			{
				Citta city = new Citta(result.getString("Localita"));
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
}
