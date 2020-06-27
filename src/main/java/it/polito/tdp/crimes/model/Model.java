package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private EventsDao dao;
	private List<String> categorie;
	private List<Integer> mesi;
	
	//grafo semplice pesato non orientato
	Graph<String, DefaultWeightedEdge> grafo;
	
	private List<String> tipologie;
	private List<Collegamento> collegamenti;
	
	public Model() {
		this.dao = new EventsDao();
		this.categorie = this.dao.getAllCategories();
		this.mesi = dao.getAllMonths();
	}
	
	public List<String> prendiCategorie(){
		return this.categorie;
	}
	
	public List<Integer> prendiMesi(){
		return this.mesi;
	}
	
	public void creaGrafo(String input, Integer mese) {
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//VERTICI
		this.tipologie = this.dao.getTypologies(input, mese);
		
		Graphs.addAllVertices(this.grafo, this.tipologie);
		
		//ARCHI
		this.collegamenti = dao.getCollegamenti(input, mese);
		
		for(Collegamento c: this.collegamenti) {
			Graphs.addEdge(this.grafo, c.getTipo1(), c.getTipo2(), c.getPeso());
		}
		
	}
	
	public int numeroVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int numeroArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public float pesoMedioGrafo() {
		
		float media = 0;
		
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			media += this.grafo.getEdgeWeight(e);
		}
		
		return media/this.grafo.edgeSet().size();
	}
	
	public String superioreAllaMedia(){
		
		float media = this.pesoMedioGrafo();
		String stampa = "";
		
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e) > media) {
				stampa += this.grafo.getEdgeSource(e)+", "+this.grafo.getEdgeTarget(e)+" --> "+this.grafo.getEdgeWeight(e)+"\n";
			}
		}
		
		return stampa;
	}
	
	//-------PUNTO 2-------
	List<String> soluzione;
	String start = "";
	String end = "";
	
	public List<Collegamento> elencoArchi(){
		return this.collegamenti;
	}
	
	public String calcolaPercorso(Collegamento arco){
		
		this.start = arco.getTipo1();
		this.end = arco.getTipo2();
		
		this.soluzione = new ArrayList<>();
		
		List<String> parziale = new ArrayList<>();
		
		parziale.add(start);
		
		ricorsione(parziale, 0);
		
		String stampa = "";
		for(String s: this.soluzione) {
			stampa += s.toString()+"\n";
		}
		
		return stampa;
	}
	
	public void ricorsione(List<String> parziale, int livello) {
		//caso finale
		if(parziale.get(parziale.size()-1).equals(end) && parziale.size()> this.soluzione.size()) {
			this.soluzione = new ArrayList<>(parziale);
		}
		
		//caso intermedio
		String ultimoAggiunto = parziale.get(parziale.size()-1);
		List<String> vicini = Graphs.neighborListOf(this.grafo, ultimoAggiunto);
		
		for(String s: vicini) {
			if(!parziale.contains(s)) {
				parziale.add(s);
				ricorsione(parziale, livello+1);
				parziale.remove(s);
			}
		}
		
	}
}
