package org.dspace.storage.rdbms;

//Imports
import java.io.File;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.authority.ArvoPersonAuthorityValue;
//import org.dspace.app.tablecsv.DSpaceTableCSV;
import org.dspace.core.ConfigurationManager;
import org.postgresql.util.PSQLException;

/**
 * @author Sergio Nieto CaramÃ©s
 * @author AdÃ¡n RomÃ¡n Ruiz
 */

public class DB {
	public static DB db = null;
	// ICV
	private Connection autoritiesConection;
	private Connection dspaceConection;
	private final static Logger log = Logger.getLogger(DB.class);

	// Constructor

	private DB(String driver, String url, String username, String password,
		String driver2, String url2, String username2, String password2) {
	    boolean isConnected = false;
	    try {
		Class.forName(driver);
		dspaceConection = DriverManager.getConnection(url, username,
			password);
		autoritiesConection = DriverManager.getConnection(url2, username2,
			password2);
		if (dspaceConection != null && autoritiesConection != null) {
		    isConnected = true;
		}
		System.out.println("isConnected? " + isConnected);
		log.debug("isConnected? " + isConnected);
	    } catch (SQLException ex) {
		log.error(ex);
		ex.printStackTrace();
	    } catch (ClassNotFoundException ex) {
		log.error(ex);
		ex.printStackTrace();
	    } catch (Exception e) {
		log.error("Se ha producido una excepcion", e);
	    }
	}

	public static DB getInstance() {
	    if (db == null) {
		String driver = ConfigurationManager.getProperty("db.driver");
		String url = ConfigurationManager.getProperty("db.url");
		String username = ConfigurationManager.getProperty("db.username");
		String password = ConfigurationManager.getProperty("db.password");
		String driver2 = ConfigurationManager.getProperty("db2.driver");
		String url2 = ConfigurationManager.getProperty("db2.url");
		String username2 = ConfigurationManager.getProperty("db2.username");
		String password2 = ConfigurationManager.getProperty("db2.password");
		log.debug("Conexion a la base de datos: " + driver + ";" + url + ";" + username + ";" + password);
		db = new DB(driver, url, username, password, driver2, url2,username2, password2);
	    }
	    return db;
	}

	private void reconect(){
    	closeConnection();
    	try{
    		String driver = ConfigurationManager.getProperty("db.driver");
		    String url = ConfigurationManager.getProperty("db.url");
		    String username = ConfigurationManager.getProperty("db.username");
		    String password = ConfigurationManager.getProperty("db.password");
		    String driver2 = ConfigurationManager.getProperty("db2.driver");
		    String url2 = ConfigurationManager.getProperty("db2.url");
		    String username2 = ConfigurationManager.getProperty("db2.username");
		    String password2 = ConfigurationManager.getProperty("db2.password");
		    Class.forName(driver);
		    dspaceConection = DriverManager.getConnection(url, username, password);
		    autoritiesConection= DriverManager.getConnection(url2, username2, password2);
    	} catch (SQLException ex) {
    	    log.error(ex);
    	    ex.printStackTrace();
    	} catch (ClassNotFoundException ex) {
    	    log.error(ex);
    	    ex.printStackTrace();
    	}catch (Exception e){
    	    log.error("Se ha producido una excepcion",e);
    	}
    }
	

	public Vector<ArvoPersonAuthorityValue> getPersons(String text) {
	    Vector<ArvoPersonAuthorityValue> persons = new Vector<ArvoPersonAuthorityValue>();
	    if(StringUtils.isEmpty(text)){
		return persons;
	    }
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    //		String querySoloApe=   "SELECT id, apellidos, nombre, centro FROM persona WHERE translate(lower(apellidos),'Ã¡Ã©Ã­Ã³ÃºÃ±Ã¼','aeiounu') ILIKE translate(lower(?),'Ã¡Ã©Ã­Ã³ÃºÃ±Ã¼','aeiounu') ORDER BY apellidos, nombre";
	    //		String queryApeYNombre="SELECT id, apellidos, nombre, centro FROM persona WHERE translate(lower(apellidos),'Ã¡Ã©Ã­Ã³ÃºÃ±Ã¼','aeiounu') ILIKE translate(lower(?),'Ã¡Ã©Ã­Ã³ÃºÃ±Ã¼','aeiounu') and translate(lower(nombre),'Ã¡Ã©Ã­Ã³ÃºÃ±Ã¼','aeiounu') ILIKE translate(lower(?),'Ã¡Ã©Ã­Ã³ÃºÃ±Ã¼','aeiounu') ORDER BY apellidos, nombre";
	    String queryTodasParcialesPre= " "+ ConfigurationManager.getProperty("sql.author.queryTodasParcialesPre") +" ";
	    String queryTodasParcialesRep= " "+ ConfigurationManager.getProperty("sql.author.queryTodasParcialesRep") +" ";
	    String queryTodasParcialesPost= " "+ ConfigurationManager.getProperty("sql.author.queryTodasParcialesPost") +" ";
	    StringBuffer query=new StringBuffer();
	    String[] textSplitted=text.split("\\s+|,\\s*");
	    try {
		try {
		    if (text.equals(""))
			return persons;
		    else if(textSplitted.length>0){
			query.append(queryTodasParcialesPre);
			for(int i=0;i<textSplitted.length;i++){
			    if(i!=0){
				query.append(" AND ");
			    }
			    query.append(queryTodasParcialesRep);
			}
			query.append(queryTodasParcialesPost);
			try{
			    pstmt=getAutoritiesConection().prepareStatement(query.toString());
			    for(int i=0;i<textSplitted.length;i++){
				    pstmt.setString(i+1, textSplitted[i].trim());
				}
			    rs =pstmt.executeQuery();
			} catch (SQLException e) {
			    if(pstmt!=null){
				pstmt.close();
			    }
			    if(rs!=null){
				rs.close();
			    }
			    // Intentamos reconectar
			    reconect();
			    pstmt=getAutoritiesConection().prepareStatement(query.toString());
			    for(int i=0;i<textSplitted.length;i++){
				    pstmt.setString(i+1, textSplitted[i].trim());
				}
			    rs =pstmt.executeQuery();
			}

			
		    }

		  

		    try {
			while (rs.next()) {
			    ArvoPersonAuthorityValue apav=new ArvoPersonAuthorityValue();
			    apav.setFirstName(rs.getString("nombre"));
			    apav.setLastName(rs.getString("apellidos"));
			    apav.setValue(apav.getName());
			    apav.setId(rs.getString("id"));
			    persons.add(apav);
			    // parametros extra
			    String authorExtraParams=ConfigurationManager.getProperty("sql.author.extraparams");
			    if(StringUtils.isNotBlank(authorExtraParams)){
				String[] params= authorExtraParams.split(",");
				if(params!=null && params.length>0){
				    for(int i=0;i<params.length;i++){
					apav.getExtraInfo().put(params[i], rs.getString(params[i]));  
				    }
				}
			    }
			}

		    } finally {
			if (rs != null)
			    rs.close();
		    }
		} catch (Exception ex) {
		    ex.printStackTrace();
		    log.error(ex);
		} finally {
		    if (pstmt != null)
			pstmt.close();
		}

	    } catch (SQLException ex) {
		log.error(ex);
	    }
	    return persons;
	}
	
	/* Solo usado para bestmatch, macheo exacto*/
	public Vector getPersons(String sql, String key) {
	    Vector persons = new Vector();
	    Statement stmt = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
		try {
		    		
		    if (key.equals(""))
			return persons;
		    else {
			// Solo nos interesa coger el campo apellidos
			String[] newKey = key.split(",");
			sql = sql.replace("?", "'"+newKey[0]+"'");
			// en caso de que lleve un segundo parÃ¡metro
			if (sql.contains("@") && newKey.length>1){
			    sql = sql.replace("@", "'"+newKey[1].replaceAll("^\\s*","")+"'");
			}else{
			    sql = sql.replace("@", "''");
			}
		    
		    }
		    try{
			stmt = getAutoritiesConection().createStatement();
			rs = stmt.executeQuery(sql);
		    } catch (SQLException e) {
			// Intentamos reconectar
			reconect();
			stmt = getAutoritiesConection().createStatement();
			rs = stmt.executeQuery(sql);
		    }
		    
		    int nCols = rs.getMetaData().getColumnCount();
		    String[] values = new String[nCols];
		    try {
			while (rs.next()) {
			    for (int i = 0; i < nCols; i++) {
				if (rs.getString(i + 1) != null)
				    values[i] = rs.getString(i + 1);
				else
				    values[i] = "";
			    }
			    persons.add(values[1] + ", " + values[2] + " ("
				    + values[0] + ")");

			}

		    } finally {
			if (rs != null)
			    rs.close();
		    }
		} catch (Exception ex) {
		    ex.printStackTrace();
		    log.error(ex);
		} finally {
		    if (pstmt != null)
			pstmt.close();
		}

	    } catch (SQLException ex) {
		log.error(ex);
	    }
	    return persons;
	}

	public boolean existTable(String tablename) throws SQLException {
	    boolean result = false;

	    Statement stmt = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
		String sql;
		String dbName = getAutoritiesConection().getMetaData().getDatabaseProductName().toLowerCase();
		if(dbName.contains("oracle")){
		    sql = "select LEAST(count(*),1) from all_objects where object_type in ('TABLE','VIEW') and object_name = '" + tablename.toUpperCase() + "'";
		}else{
		    sql = "SELECT EXISTS(SELECT * FROM information_schema.tables WHERE table_schema = 'public' AND table_name ilike '" + tablename + "');";
		}
		try{
			stmt = getAutoritiesConection().createStatement();
			rs = stmt.executeQuery(sql);
		    } catch (SQLException e) {
			// Intentamos reconectar
			reconect();
			stmt = getAutoritiesConection().createStatement();
			rs = stmt.executeQuery(sql);
		    }
		try {
		    rs.next();
		    result = rs.getBoolean(1);
		} finally {
		    if (rs != null)
			rs.close();
		}

	    } catch (SQLException ex) {
		ex.printStackTrace();
	    } finally {
		if (pstmt != null) {
		    pstmt.close();
		}
	    }
	    return result;

	}
	
	

	public String[] executeQueryUnique(String sql, String... params)
		throws SQLException {
	    String[] result = null;

	    Statement stmt = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {

		for (int i = 0; i < params.length; i++) {
		    sql = sql.replace("?", params[i]);
		}
		try{
			stmt = getAutoritiesConection().createStatement();
			rs = stmt.executeQuery(sql);
		    } catch (SQLException e) {
			// Intentamos reconectar
			reconect();
			stmt = getAutoritiesConection().createStatement();
			rs = stmt.executeQuery(sql);
		    }
		try {
		    while (rs.next()) {
			int nCols = rs.getMetaData().getColumnCount();
			result = new String[nCols];
			for (int i = 0; i < nCols; i++) {
			    result[i] = rs.getString(i + 1);
			}
		    }
		} finally {
		    if (rs != null)
			rs.close();
		}

	    } catch (PSQLException ex) {
		log.error("Fallo en consulta:" + sql + " Con claves de busqueda:"
			+ params);
		ex.printStackTrace();
	    } catch (SQLException ex) {
		log.error("Fallo en consulta:" + sql + " Con claves de busqueda:"
			+ params);
		ex.printStackTrace();
	    } finally {
		if (pstmt != null) {
		    pstmt.close();
		}
	    }
	    return result;
	}
	// cerramos la conexiÃ³n
	public void closeConnection() {
	    if (autoritiesConection != null) {
		try {
		    autoritiesConection.close();
		} catch (SQLException sqle) {
		}
	    }
	    if (autoritiesConection != null) {
		try {
		    dspaceConection.close();
		} catch (SQLException sqle) {
		}
	    }
	}

	public int executeUpdate(String insert) throws SQLException {
	    int result = 0;
	    Statement stmt = null;
	    try {
		try{
			stmt = getAutoritiesConection().createStatement();
			result = stmt.executeUpdate(insert);
		    } catch (SQLException e) {
			// Intentamos reconectar
			reconect();
			if(stmt!=null){
			    stmt.close();
			}
			stmt = getAutoritiesConection().createStatement();
			result = stmt.executeUpdate(insert);
		    }
	    } finally {
		stmt.close();
	    }
	    return result;
	}
	
	public int emptyTable(String tablename) throws SQLException {
	    int result = 0;
	    Statement stmt = null;
	    try {
		String delete = "delete from " + tablename ;
		try{
			stmt = getAutoritiesConection().createStatement();
			result = stmt.executeUpdate(delete);
		    } catch (SQLException e) {
			// Intentamos reconectar
			reconect();
			if(stmt!=null){
			    stmt.close();
			}
			stmt = getAutoritiesConection().createStatement();
			result = stmt.executeUpdate(delete);
		    }
	    } finally {
		stmt.close();
	    }
	    return result;
	}

	public HashMap<String, String> getDatatypes(String tablename) {

	    HashMap<String, String> datosColumnas = new HashMap<String, String>();
	    Statement stmt = null;
	    ResultSet rs = null;
	    try {
		
		String sql;
		String dbName = getAutoritiesConection().getMetaData().getDatabaseProductName().toLowerCase();
		if(dbName.contains("oracle")){
		    sql="select column_name, data_type from user_tab_columns where table_name = '" + tablename.toUpperCase() + "';";
		}else{
		    sql="select column_name, data_type from information_schema.columns where table_name ilike '" + tablename + "';";
		}
		try{
		    stmt = getAutoritiesConection().createStatement();
		    rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
		    if(stmt!=null){
			stmt.close();
		    }
		    // Intentamos reconectar
		    reconect();
		    stmt = getAutoritiesConection().createStatement();
		    rs = stmt.executeQuery(sql);
		}
		while (rs.next()) {
		    datosColumnas.put(rs.getString(1), rs.getString(2));
		}
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } finally {
		try {
		    if (rs != null) {
			rs.close();
		    }
		    if (stmt != null) {
			stmt.close();
		    }
		} catch (SQLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }
	    return datosColumnas;
	}
	
	public ArrayList<String> getTableNames() {

	    ArrayList<String> tablenames = new ArrayList<String>();
	    Statement stmt = null;
	    ResultSet rs = null;
	    try {
		String sql;
		String dbName = getAutoritiesConection().getMetaData().getDatabaseProductName().toLowerCase();
		if(dbName.contains("oracle")){
		    sql="select OBJECT_NAME from all_objects where object_type in ('TABLE') and OWNER like 'USER%' and OBJECT_NAME like 'vc_%';";
		}else{
		    sql="SELECT tablename FROM pg_catalog.pg_tables where schemaname='public' and tablename like 'vc_%';;";
		}
		try{
		    stmt = getAutoritiesConection().createStatement();
		    rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
		    if(stmt!=null){
			stmt.close();
		    }
		    // Intentamos reconectar
		    reconect();
		    stmt = getAutoritiesConection().createStatement();
		    rs = stmt.executeQuery(sql);
		}
		while (rs.next()) {
		    tablenames.add(rs.getString(1).substring("vc_".length()));
		}
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } finally {
		try {
		    if (rs != null) {
			rs.close();
		    }
		    if (stmt != null) {
			stmt.close();
		    }
		} catch (SQLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }
	    return tablenames;
	}

	public ArrayList<ArrayList<String>> getFullTable(String tableName)
		throws SQLException {
	    ArrayList<ArrayList<String>> filas = new ArrayList<ArrayList<String>>();

	    Statement stmt = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {

		try{
		    stmt = getAutoritiesConection().createStatement();
		    rs = stmt.executeQuery("select * from " + tableName);
		} catch (SQLException e) {
		    if(stmt!=null){
			stmt.close();
		    }
		    // Intentamos reconectar
		    reconect();
		    stmt = getAutoritiesConection().createStatement();
		    rs = stmt.executeQuery("select * from " + tableName);
		}
		try {
		    // nombres de columnas
		    ArrayList<String> fila = new ArrayList<String>();
		    int nCols = rs.getMetaData().getColumnCount();
		    for (int i = 0; i < nCols; i++) {
			fila.add(rs.getMetaData().getColumnLabel(i + 1));
		    }
		    filas.add(fila);
		    while (rs.next()) {
			fila = new ArrayList<String>();
			for (int i = 0; i < nCols; i++) {
			    fila.add(rs.getString(i + 1));
			}
			filas.add(fila);
		    }
		} finally {
		    if (rs != null)
			rs.close();
		}

	    } catch (SQLException ex) {
		ex.printStackTrace();
	    } finally {
		if (pstmt != null) {
		    pstmt.close();
		}
	    }
	    return filas;
	}
	
	public Vector getRevistas(String sql, String key) {
	    Vector revistas = new Vector();

	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
		try {
		    if (key.equals(""))
			return revistas;
		    try{

			pstmt = getAutoritiesConection().prepareStatement(sql);
			pstmt.setString(1, "%" + key + "%");
			rs = pstmt.executeQuery();
		    } catch (SQLException e) {
			// Intentamos reconectar
			reconect();
			if(pstmt!=null){
			    pstmt.close();
			}
			pstmt = getAutoritiesConection().prepareStatement(sql);
			pstmt.setString(1, "%" + key + "%");
			rs = pstmt.executeQuery();
		    }


		    int nCols = rs.getMetaData().getColumnCount();
		    String[] values = new String[nCols];
		    try {
			while (rs.next()) {
			    for (int i = 0; i < nCols; i++) {
				if (rs.getString(i + 1) != null)
				    values[i] = rs.getString(i + 1);
				else
				    values[i] = "";
			    }
			    revistas.add(values[1] + " (" + values[0] + ")");

			}

		    } finally {
			if (rs != null)
			    rs.close();
		    }
		} catch (Exception ex) {
		    ex.printStackTrace();
		    log.error(ex);
		} finally {
		    if (pstmt != null)
			pstmt.close();
		}

	    } catch (SQLException ex) {
		log.error(ex);
	    }
	    return revistas;
	}

	public String select(String query) {
		String message = "";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			try{
			    stmt = getAutoritiesConection().createStatement();
			    rs = stmt.executeQuery(query);
			} catch (SQLException e) {
			    if(stmt!=null){
				stmt.close();
			    }
			    // Intentamos reconectar
			    reconect();
			    stmt = getAutoritiesConection().createStatement();
			    rs = stmt.executeQuery(query);
			}
			while (rs.next()) {
				message += rs.getString(1);// devuelve el primer parametro de la
											// peticion sql siempre que sea
											// String

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
				    stmt.close();
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return message;
	}

	public void createTable(String name, List<String> headings,
			List<Integer> sizes) {
		boolean result = false;

		Statement stmt = null;
		PreparedStatement pstmt = null;

		try {
			StringBuffer query = new StringBuffer();
			query.append("CREATE TABLE ").append(name).append("( ");
			for (int i = 0; i < headings.size(); i++) {
				if (i != 0) {
					query.append(",");
				}
				query.append(headings.get(i)).append(" ")
						.append("character varying(").append(sizes.get(i))
						.append(")");
			}
			query.append(");");
			StringBuffer owner = new StringBuffer("ALTER TABLE ")
					.append(name)
					.append(" OWNER TO ")
					.append(ConfigurationManager
							.getBooleanProperty("db.username")).append(";");

			try{
			    stmt = getAutoritiesConection().createStatement();
			    stmt.executeUpdate(query.toString());
			    stmt.executeUpdate(owner.toString());
			} catch (SQLException e) {
			    if(stmt!=null){
				stmt.close();
			    }
			    // Intentamos reconectar
			    reconect();
			    stmt = getAutoritiesConection().createStatement();
			    stmt.executeUpdate(query.toString());
			    stmt.executeUpdate(owner.toString());
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {

		}
	}
	
	public DatosPersona getDatosPersona(String sql, String... params) throws SQLException {
    	
    	DatosPersona datosPersona=new DatosPersona();
    	Statement stmt = null;
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	try {

    	    for(int i=0;i<params.length;i++){
    		sql = sql.replace("?", params[i]);
    	    }		    
		try{
		    stmt = getAutoritiesConection().createStatement();
		    rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
		    if(stmt!=null){
			stmt.close();
		    }
		    // Intentamos reconectar
		    reconect();
		    stmt = getAutoritiesConection().createStatement();
		    rs = stmt.executeQuery(sql);
		}
    	    try {
    		while (rs.next()) {
    			datosPersona.orcid=rs.getString("orcid");    			
    			datosPersona.googleScholar=rs.getString("googleid");
    			datosPersona.researcherID= rs.getString("researcherid");
    			datosPersona.scopusID= rs.getString("scopusid");
    			datosPersona.plumx= rs.getString("plumx");
    			datosPersona.pivot= rs.getString("pivot");
    			datosPersona.cvlac= rs.getString("cvlac");
    		}
    	    } finally {
    		if (rs != null)
    		    rs.close();
    	    }

    	} catch (SQLException ex) {
    	    ex.printStackTrace();
    	} finally {
    	    if (pstmt != null){
    		pstmt.close();
    	    }
    	}
    	return datosPersona;
        }

	/**
	 * No cerrar la conexion
	 * @return
	 * @throws SQLException 
	 */
	public final Connection getAutoritiesConection() throws SQLException {
	    if(autoritiesConection==null || autoritiesConection.isClosed()){
		if(autoritiesConection!=null){
		    autoritiesConection.close();
		}
		String url2 = ConfigurationManager.getProperty("db2.url");
		String username2 = ConfigurationManager.getProperty("db2.username");
		String password2 = ConfigurationManager.getProperty("db2.password");
		autoritiesConection = DriverManager.getConnection(url2, username2,password2);
	    }
	    return autoritiesConection;
	}
	/**
	 * No cerrar la conexion
	 * @param autoritiesConection
	 * @throws SQLException 
	 */
	public final Connection getDspaceConection() throws SQLException {
	    if(dspaceConection==null || !dspaceConection.isClosed()){
		if(dspaceConection!=null){
		    dspaceConection.close();
		}
		String url = ConfigurationManager.getProperty("db.url");
		String username = ConfigurationManager.getProperty("db.username");
		String password = ConfigurationManager.getProperty("db.password");
		dspaceConection = DriverManager.getConnection(url, username,password);
	    }
	    return dspaceConection;
	}
	
	public String getAuthorityID(String authorityEmail) throws SQLException{
		int id = 0;
		
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			stmt = autoritiesConection.createStatement();
			String sql = ConfigurationManager.getProperty("select.idFromAuthority");
			rs = stmt.executeQuery(sql.replace("?", authorityEmail));
			try {
				while (rs.next()) {
					id = rs.getInt("id");
				}
			} finally {
				if (rs != null)
					rs.close();
			}

		} catch (SQLException ex) {
			log.info("Error en getAutoridades:" + ex);
			ex.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		} 
		
		return String.valueOf(id);
	}
	
	public String getAuthorityEmail(String authorityID) throws SQLException{
		String email = "";
		
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			stmt = autoritiesConection.createStatement();
			String sql = ConfigurationManager.getProperty("select.emailFromAuthority");
			rs = stmt.executeQuery(sql.replace("?", authorityID));
			try {
				while (rs.next()) {
					email = rs.getString("email");
				}
			} finally {
				if (rs != null)
					rs.close();
			}

		} catch (SQLException ex) {
			log.info("Error en getAutoridades:" + ex);
			ex.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		} 
		
		return email;
	}

	public Map<String, String> getOrcidID(String epersonEmail) throws SQLException {
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		Map<String, String> apiCredentials = new HashMap<>();
		try {
			stmt = autoritiesConection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM vc_persona WHERE email ='" + epersonEmail + "'");
			try {
				if (rs.next()) {
					apiCredentials.put("orcid", rs.getString("orcid"));
					;
					apiCredentials.put("access_token", rs.getString("access_token"));
				}
			} finally {
				if (rs != null)
					rs.close();
			}

		} catch (SQLException ex) {
			log.info("Error en getAutoridades:" + ex);
			ex.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return apiCredentials;
	}
	
	public boolean isAuthorOrcid(String epersonEmail) throws SQLException {
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			stmt = autoritiesConection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM vc_persona WHERE email ='" + epersonEmail + "'");
			try {
				if (rs.next()){
				    if(StringUtils.isNotBlank(rs.getString("access_token"))){
					return true;
				    }
				}
			} finally {
				if (rs != null)
					rs.close();
			}

		} catch (SQLException ex) {
			log.info("Error en getAutoridades:" + ex);
			ex.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return false;
	}
	
	public class DatosPersona {
		
		public String orcid;		
		public String googleScholar;
		public String researcherID;
		public String scopusID;
		public String cvlac;
		public String pivot;
		public String plumx;

		public DatosPersona() {

		}

		public DatosPersona(String orcid, String googleScholar,String researcherID, String scopusID, String cvlac, String pivot, String plumx) {
			this.orcid = orcid;			
			this.googleScholar = googleScholar;
			this.researcherID = researcherID;
			this.scopusID = scopusID;
			this.cvlac = cvlac;
			this.pivot = pivot;
			this.plumx = plumx;
		}
		
		public String getResearcherID() {
			return researcherID;
		}

		public void setResearcherID(String researcherID) {
			this.researcherID = researcherID;
		}

		public String getScopusID() {
			return scopusID;
		}

		public void setScopusID(String scopusID) {
			this.scopusID = scopusID;
		}

		public String getGoogleScholar() {
			return googleScholar;
		}

		public void setGoogleScholar(String googleScholar) {
			this.googleScholar = googleScholar;
		}

		public String getOrcid() {
			return orcid;
		}

		public void setOrcid(String orcid) {
			this.orcid = orcid;
		}

		public String getCvlac() {
			return cvlac;
		}

		public void setCvlac(String cvlac) {
			this.cvlac = cvlac;
		}

		public String getPivot() {
			return pivot;
		}

		public void setPivot(String pivot) {
			this.pivot = pivot;
		}

		public String getPlumx() {
			return plumx;
		}

		public void setPlumx(String plumx) {
			this.plumx = plumx;
		}

	}

}// class
