package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

public class ConnectDB
{
	private static final String jdbcUrl = "jdbc:mariadb://localhost/meteo";
	private static final String user = "root";
	private static final String password = "root";
	private static HikariDataSource dataSource;
	
	static
	{
		dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(jdbcUrl);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
	}

	public static Connection getConnection() 
	{
		try 
		{
			Connection connection = dataSource.getConnection();
			return connection;
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
			throw new RuntimeException("Cannot get a connection to: " + jdbcUrl, sqle);
		}
	}
	
	public static void close(AutoCloseable... resources) throws SQLException
	{
		for(var x : resources)
		{
			try
			{
				x.close();
			}
			catch(Exception e)
			{
				throw new SQLException("Error closing resource: " + x.toString(), e);
			}
		}
	}

}
