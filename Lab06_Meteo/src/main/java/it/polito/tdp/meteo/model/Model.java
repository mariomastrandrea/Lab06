package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	private List <Citta> leCitta;
	private List <Citta> best;
	
	
	/**
	 * Tutte le città presenti nel database. La lista viene letta al momento della costruzione del model
	 * @return lista delle città presenti
	 */
	public List<Citta> getLeCitta(){
		return leCitta;
	}
	
	// of course you can change the String output with what you think works best
	//public String getUmiditaMedia(int mese) {
	/**
	 * Calcolo dell'umidità media di una città in un mese
	 * @param mese
	 * @param citta
	 * @return valore dell'umidità media
	 */
	public Double getUmiditaMedia(int mese, Citta citta)
	{
		MeteoDAO dao = new MeteoDAO();
		return dao.getUmiditaMedia(mese, citta);
	}
	
	public Model() {
		MeteoDAO dao = new MeteoDAO();
		this.leCitta = dao.getAllCitta();
	}

	// of course you can change the String output with what you think works best
	//public String trovaSequenza(int mese) {
	//	return "TODO!";
	//}	
	/**
	 * Calcola la sequenza ottimale di visita delle città nel mese specificato
	 * @param mese il mese da analizzare
	 * @return la lista delle città da visitare nei primi 15 giorni del mese
	 */
	public List<Citta> trovaSequenza(int mese){
		List <Citta> parziale = new ArrayList<>();
		this.best = null;
		
		MeteoDAO dao = new MeteoDAO();
		
		//carica dentro ciascuna delle leCitta la lista dei rilevamenti nel mese considerato (e solo quello)
		for (Citta c: leCitta) {
			c.setRilevamenti(dao.getAllRilevamentiLocalitaMese(mese, c)); 
		}
		//System.out.println("RICERCA MESE "+Integer.toString(mese));
		cerca(parziale,0);
		return best;
			
	}

	
	/**
	 * Procedura ricorsiva per il calcolo delle città ottimali
	 * Per informazioni sull'impostazione della ricorsione, vedere il file logica_della_ricorsione.txt
	 * nella cartella di progetto 
	 * @param parziale soluzione parziale in via di costruzione
	 * @param livello livello della ricorsione, cioè il giorno a cui si sta cercando di definire la città
	 */
	private void cerca(List<Citta> parziale, int livello) {
		
		if (livello== NUMERO_GIORNI_TOTALI) {
			//caso terminale
			Double costo = calcolaCosto(parziale);
			if (best ==null || costo < calcolaCosto(best)) {
				//System.out.format("%f %s\n", costo, parziale);
				best = new ArrayList<>(parziale);
			}
			//System.out.println(parziale);
		}else {
			//caso intermedio
			for (Citta prova: leCitta) {
				if (aggiuntaValida(prova,parziale)) {
					parziale.add(prova);
					cerca(parziale, livello+1);
					parziale.remove(parziale.size()-1);
				}
			}			
		}
	}

	/**
	 * Calcola il costo di una determinata soluzione (totale)
	 * 
	 * Stiamo facendo l'assunzione semplificativa vhe tutti i dati/giorni del mese siano presenti nel database
	 * ma nel nostro esempio ciò non accade (in alcuni giorni il dato è mancante).
	 * 
	 * @param parziale la soluzione (totale) proposta
	 * @return il valore del costo, che tiene conto delle umidità nei 15 giorni e del costo di cambio città
	 */
	private Double calcolaCosto(List<Citta> parziale) {
		double costo = 0.0;
		//sommatoria delle umidità in ciascuna città, considerando il rilevamento del giorno giusto
		//SOMMA parziale.get(giorno-1).getRilevamenti().get(giorno-1)
		for (int giorno=1; giorno<=NUMERO_GIORNI_TOTALI; giorno++) {
			//dove mi trovo
			Citta c = parziale.get(giorno-1);
			//che umidità ho in quel giorno in quella città?
			double umid = c.getRilevamenti().get(giorno-1).getUmidita();
			costo+=umid;
		}
		//poi devo sommare 100*numero di volte in cui cambio città
		for (int giorno=2; giorno<=NUMERO_GIORNI_TOTALI; giorno++) {
			//dove mi trovo
			if(!parziale.get(giorno-1).equals(parziale.get(giorno-2))) {
				costo +=COST;
			}
		}
		return costo;
	}

	/**
	 * Verifica se, data la soluzione {@code parziale} già definita, sia lecito
	 * aggiungere la città {@code prova}, rispettando i vincoli sui numeri giorni
	 * minimi e massimi di permanenza.
	 * 
	 * @param prova la città che sto cercando di aggiungere
	 * @param parziale la sequenza di città già composta
	 * @return {@code true} se {@code prova} è lecita, {@code false} se invece viola
	 *         qualche vincolo (e quindi non è lecita)
	 */
	private boolean aggiuntaValida(Citta prova, List<Citta> parziale) {
		
		//verifica giorni massimi
		//contiamo quante volte la città 'prova' era già apparsa nell'attuale lista costruita fin qui
		int conta = 0;
		for (Citta precedente:parziale) {
			if (precedente.equals(prova))
				conta++; 
		}
		if (conta >=NUMERO_GIORNI_CITTA_MAX)
			return false;
		
		// verifica dei giorni minimi
		if (parziale.size()==0) //primo giorno posso inserire qualsiasi città
				return true;
		if (parziale.size()==1 || parziale.size()==2) {
			//siamo al secondo o terzo giorno, non posso cambiare
			//quindi l'aggiunta è valida solo se la città di prova coincide con la sua precedente
			return parziale.get(parziale.size()-1).equals(prova); 
		}
		//nel caso generale, se ho già passato i controlli sopra, non c'è nulla che mi vieta di rimanere nella stessa città
		//quindi per i giorni successivi ai primi tre posso sempre rimanere
		if (parziale.get(parziale.size()-1).equals(prova))
			return true; 
		// se cambio città mi devo assicurare che nei tre giorni precedenti sono rimasto fermo 
		if (parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) 
		&& parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)))
			return true;
			
		return false;
		
	}



}
