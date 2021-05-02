package it.polito.tdp.meteo;

import java.net.URL;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.MeteoModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import it.polito.tdp.meteo.model.Month;
import it.polito.tdp.meteo.model.Solution;

public class FXMLController 
{
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Year> yearsComboBox;
    
    @FXML
    private ComboBox<Month> monthsComboBox;
    
    @FXML
    private ComboBox<Integer> firstDayComboBox;

    @FXML
    private ComboBox<Integer> numGiorniComboBox;

    @FXML
    private Button umiditaButton;

    @FXML
    private Button calcolaButton;

    @FXML
    private TextArea resultTextArea;
    
	private MeteoModel model;
    

    @FXML
    void handleCalcolaSequenzaOttimale(ActionEvent event) 
    {
    	Year selectedYear = this.yearsComboBox.getValue();
    	
    	if(selectedYear == null)
    		throw new RuntimeException("Error: year has not been selected");
    	
    	Month selectedMonth = this.monthsComboBox.getValue();

    	if(selectedMonth == null)
    		throw new RuntimeException("Error: month has not been selected");
    	
    	Integer selectedDayOfMonth = this.firstDayComboBox.getValue();
    	
    	if(selectedDayOfMonth == null)
    		throw new RuntimeException("Error: day of month has not been selected");
    		
    	Integer selectedNumOfDays = this.numGiorniComboBox.getValue();
    	
    	if(selectedNumOfDays == null)
    		throw new RuntimeException("Error: num of days has not been selected");
    	
    	//all inputs ok
    		
    	LocalDate startDate = LocalDate.of(selectedYear.getValue(), selectedMonth.getNum(), selectedDayOfMonth.intValue());
    	LocalDate endDate = startDate.plusDays(selectedNumOfDays-1);   
    	
    	long start = System.nanoTime();
    	Solution optSolution = this.model.computeOptimalSequencesFor(startDate, endDate);
    	long end = System.nanoTime();
    	
    	String timeMs = String.format("%.3f", (double)(end-start)/1000000.0);
    	
    	String result = this.printSolution(optSolution, selectedYear, selectedMonth, timeMs);
    	this.resultTextArea.setText(result);
    }
    
    @FXML
    void handleCalcolaUmiditaMedia(ActionEvent event) 
    {
    	Year selectedYear = this.yearsComboBox.getValue();

    	if(selectedYear == null)
    		throw new RuntimeException("Error: year has not been selected");
    	
    	Month selectedMonth = this.monthsComboBox.getValue();

    	if(selectedMonth == null)
    		throw new RuntimeException("Error: month has not been selected");
    	

    	YearMonth yearMonth = YearMonth.of(selectedYear.getValue(), selectedMonth.getNum());
    	
    	Map<Citta, Double> humidityByCity = this.model.getUmiditaMedia(yearMonth);
    	
    	if(humidityByCity.isEmpty())
    	{
    		this.resultTextArea.setText("Non esiste alcuna città per il mese selezionato");
    		return;
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("Umidità medie per il mese di ").append(selectedMonth.toString());
    	sb.append(" ").append(selectedYear).append(":\n\n");
    	sb.append(this.printHumidities(humidityByCity));
    	
    	this.resultTextArea.setText(sb.toString());
    }
    
    private String printHumidities(Map<Citta,Double> map)
    {
    	StringBuilder sb = new StringBuilder();
    	
    	for(Citta c : map.keySet())
    	{
    		if(sb.length() > 0)
    			sb.append("\n");
    		
    		sb.append("- ").append(c.getNome()).append("  =>  ");
    		
    		String humidity = String.format("%.2f", map.get(c));
    		sb.append(humidity).append("%");
    	}
    	
    	return sb.toString();
    }
 
    @FXML
    void handleYearChoice(ActionEvent event) 
    {
    	this.checkMonthAndYear();
    }
    
    @FXML
    void handleMonthChoice(ActionEvent event) 
    {
    	this.checkMonthAndYear();
    }
    
    @FXML
    void handleFirstDayChoice(ActionEvent event) 
    {
    	this.checkDays();
    }
    
    @FXML
    void handleNumGiorniChoice(ActionEvent event) 
    {
    	this.checkDays();
    }

    private void checkMonthAndYear()
	{
    	Month selectedMonth = this.monthsComboBox.getValue();
    	Year selectedYear = this.yearsComboBox.getValue();
    	
	
    	if(selectedMonth != null && selectedYear != null)
    	{
    		this.firstDayComboBox.setDisable(false);
    		this.numGiorniComboBox.setDisable(false);
    		this.umiditaButton.setDisable(false);
    		
    		YearMonth ym = YearMonth.of(selectedYear.getValue(), selectedMonth.getNum());
    		List<Integer> possibleDays = this.model.getListaFinoA(ym.lengthOfMonth());
    		
    		this.firstDayComboBox.getItems().clear();
    		this.firstDayComboBox.getItems().addAll(possibleDays);
    	}
    	else 
    	{
    		this.firstDayComboBox.setDisable(true);
    		this.numGiorniComboBox.setDisable(true);
    		this.umiditaButton.setDisable(true);
		}
	}
    
    private void checkDays()
	{
    	Integer selectedFirstDay = this.firstDayComboBox.getValue();
    	Integer selectedNumGiorni = this.numGiorniComboBox.getValue();
    	
	
    	if(selectedFirstDay != null && selectedFirstDay != 0
    			&& selectedNumGiorni != null && selectedNumGiorni != 0)
    	{
    		this.calcolaButton.setDisable(false);
    	}
    	else 
    	{
	    	this.calcolaButton.setDisable(true);
		}
	}

    @FXML
    void initialize() 
    {
        assert yearsComboBox != null : "fx:id=\"yearsComboBox\" was not injected: check your FXML file 'Scene_lab06.fxml'.";
        assert monthsComboBox != null : "fx:id=\"monthsComboBox\" was not injected: check your FXML file 'Scene_lab06.fxml'.";
        assert umiditaButton != null : "fx:id=\"umiditaButton\" was not injected: check your FXML file 'Scene_lab06.fxml'.";
        assert calcolaButton != null : "fx:id=\"calcolaButton\" was not injected: check your FXML file 'Scene_lab06.fxml'.";
        assert resultTextArea != null : "fx:id=\"resultTextArea\" was not injected: check your FXML file 'Scene_lab06.fxml'.";
        assert firstDayComboBox != null : "fx:id=\"firstDayComboBox\" was not injected: check your FXML file 'Scene_lab06.fxml'.";
        assert numGiorniComboBox != null : "fx:id=\"numGiorniComboBox\" was not injected: check your FXML file 'Scene_lab06.fxml'.";
    }
    
    private String printSolution(Solution solution, Year selectedYear, Month selectedMonth, String time)
    {
    	Set<List<Citta>> optSequences = solution.getOptimalSequences();
    	int minCost = solution.getMinCost();
    	
    	StringBuilder sb = new StringBuilder();

    	if(optSequences.isEmpty())
    	{
    		sb.append("Non esistono percorsi ottimi per il mese di ");
    		sb.append(selectedMonth.toString()).append(" ").append(selectedYear.toString());
    		sb.append(" che rispettano tutti i vincoli specificati.");
    		
    		return sb.toString();
    	}
    	
    	int numOptimalSequences = optSequences.size();
    	int count = 0;
    	char vocal = numOptimalSequences == 1 ? 'a' : 'e';
    	
    	sb.append(numOptimalSequences).append(" sequenz").append(vocal).append(" ottim");
    	sb.append(vocal).append(" trovat").append(vocal).append(" per il mese di ");
    	sb.append(selectedMonth.toString()).append(" ").append(selectedYear.toString()).append("\n");
    	sb.append("Costo minimo: ").append(minCost).append("\n");
    	sb.append("Tempo impiegato: ").append(time).append(" microsec\n\n");
    	
    	for(List<Citta> optSequence : optSequences)
    	{
    		if(numOptimalSequences > 1)
    			sb.append("--- Sequenza ottima n. ").append(++count).append(" ---\n");
    		
    		sb.append(this.printCitiesSequence(optSequence)).append("\n\n");
    	}
    	
    	return sb.toString();
    }
    
    private String printCitiesSequence(Collection<Citta> cities)
    {
    	StringBuilder sb = new StringBuilder();
    	
    	for(Citta c : cities)
    	{
    		if(sb.length() > 0)
    			sb.append(" -> ");
    		
    		sb.append(c.getNome());
    	}
    	
    	return sb.toString();
    }
    
    public void setModel(MeteoModel newModel)
    {
    	this.model = newModel;
    	
    	List<Year> years = this.model.getAllYears();
    	this.yearsComboBox.getItems().addAll(years);
    	
    	List<Month> months = this.model.getAllMonths();
    	this.monthsComboBox.getItems().addAll(months);
    	
    	List<Integer> numDays = this.model.getNumGiorniPossibili();
    	this.numGiorniComboBox.getItems().addAll(numDays);
    }
}


