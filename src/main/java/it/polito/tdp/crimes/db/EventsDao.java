package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.crimes.model.Collegamento;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<String> getAllCategories(){
		String sql = "SELECT DISTINCT e.offense_category_id tipo" + 
				" FROM `events` e" + 
				" ORDER BY e.offense_category_id";
		List<String> lista = new ArrayList<>();
		
		try {
			Connection con = DBConnect.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				String ete = res.getString("tipo");
				
				lista.add(ete);
			}
			
			con.close();
			
		}catch(SQLException e) {
			throw new RuntimeException ("ERRORE DB: impossibile restituire le categorie.\n", e);
		}
		
		return lista;
	}
	
	public List<Integer> getAllMonths(){
		String sql = "SELECT DISTINCT MONTH(e.reported_date) dat" + 
				" FROM `events` e" +
				" ORDER BY month(e.reported_date) asc";
		List<Integer> lista = new ArrayList<>();
		
		try {
			Connection con = DBConnect.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				Integer mese = res.getInt("dat");
				
				lista.add(mese);
			}
			
			con.close();
			
		}catch(SQLException e) {
			throw new RuntimeException("ERRORE DB: impossibile restituire i  mesi.\n", e);
		}
		
		return lista;
	}
	
	public List<String> getTypologies(String input, Integer mese){
		String sql = "SELECT DISTINCT e.offense_type_id tipol" + 
				" FROM `events` e" + 
				" WHERE e.offense_category_id = ? " + 
				"		AND MONTH(e.reported_date) = ? ";
		List<String> lista = new ArrayList<>();
		
		try {
			Connection con = DBConnect.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, input);
			st.setInt(2, mese);
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				String ne = res.getString("tipol");
				
				lista.add(ne);
			}
			
			con.close();
			
		}catch(SQLException e) {
			throw new RuntimeException("ERRORE DB: impossibile prendere tipologie.\n", e);
		}
		
		return lista;
		
	}

	public List<Collegamento> getCollegamenti(String input, Integer mese){
		String sql = "SELECT e1.offense_type_id ti1, e2.offense_type_id ti2, COUNT(e1.neighborhood_id) peso " + 
				" FROM `events` e1, `events` e2" + 
				" WHERE e1.offense_category_id = e2.offense_category_id" + 
				"		AND e1.offense_category_id = ? " + 
				"		AND e1.neighborhood_id = e2.neighborhood_id" + 
				"		AND e1.offense_type_id< e2.offense_type_id" + 
				"		AND MONTH(e1.reported_date) = month(e2.reported_date)" + 
				"		AND MONTH(e1.reported_date) = ? " + 
				" GROUP BY e1.offense_type_id, e2.offense_type_id";
		List<Collegamento> lista = new ArrayList<>();
		
		try {
			Connection con = DBConnect.getConnection();
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, input);
			st.setInt(2, mese);
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				String t1 = res.getString("ti1");
				String t2 = res.getString("ti2");
				Integer peso = res.getInt("peso");
				
				Collegamento ctemp = new Collegamento(t1, t2, peso);
				
				lista.add(ctemp);
			}
			
			con.close();
			
		}catch(SQLException e) {
			throw new RuntimeException("ERRORE DB: collegamenti non riusciti.", e);
		}
		
		return lista;
		
	}
}
