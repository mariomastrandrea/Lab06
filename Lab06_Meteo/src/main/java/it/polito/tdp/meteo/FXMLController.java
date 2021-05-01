package it.polito.tdp.meteo;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.MeteoModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import it.polito.tdp.meteo.model.Month;

public class FXMLController 
{
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Month> monthsComboBox;

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

    }

    @FXML
    void handleCalcolaUmiditaMedia(ActionEvent event) 
    {
    	Month selectedMonth = this.monthsComboBox.getValue();
    	
    	if(selectedMonth == null)
    		throw new RuntimeException("Error: month has not been selected");
    	
    	Map<Citta, Double> humidityByCity = this.model.getUmiditaMedia(selectedMonth);
    	
    	if(humidityByCity.isEmpty())
    	{
    		this.resultTextArea.setText("Non esiste alcuna città per il mese selezionato");
    		return;
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("Umidità medie per il mese di ").append(selectedMonth.toString()).append(":\n\n");
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
    void handleMonthChoice(ActionEvent event) 
    {
    	if(this.monthsComboBox.getValue() != null)
    	{
    		this.umiditaButton.setDisable(false);
    		this.calcolaButton.setDisable(false);
    	}
    	else 
    	{
    		this.umiditaButton.setDisable(true);
    		this.calcolaButton.setDisable(true);
		}
    }

    @FXML
    void initialize() 
    {
        assert monthsComboBox != null : "fx:id=\"monthsComboBox\" was not injected: check your FXML file 'Scene_lab06.fxml'.";
        assert umiditaButton != null : "fx:id=\"umiditaButton\" was not injected: check your FXML file 'Scene_lab06.fxml'.";
        assert calcolaButton != null : "fx:id=\"calcolaButton\" was not injected: check your FXML file 'Scene_lab06.fxml'.";
        assert resultTextArea != null : "fx:id=\"resultTextArea\" was not injected: check your FXML file 'Scene_lab06.fxml'.";
    }
    
    public void setModel(MeteoModel newModel)
    {
    	this.model = newModel;
    	List<Month> months = this.model.getAllMonths();
    	this.monthsComboBox.getItems().addAll(months);
    }
}


