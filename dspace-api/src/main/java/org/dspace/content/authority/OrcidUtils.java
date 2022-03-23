/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.authority;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.storage.rdbms.DB;
import org.dspace.storage.rdbms.DB.DatosPersona;


/**
 * @author AdĂĄn RomĂĄn Ruiz
 * @author Sergio Nieto CaramĂŠs
 */
public class OrcidUtils{

    private static int LENGTH_KEY=30;
	
    static Hashtable<String,DatosPersona> idsCache=new Hashtable<String,DatosPersona>();
    private static Logger log = Logger.getLogger(OrcidUtils.class);
    
    /**
     * Obtiene el idOrcid a partir del codigo de autoridad
     */
    public static String getIdOrcid(String clave) {
    	boolean cacheDisabled= ConfigurationManager.getBooleanProperty("disableIdOrcidCache", false);	
    	if(!cacheDisabled && idsCache.containsKey(clave)){
    		return idsCache.get(clave).orcid;
    	}
    	DB db = DB.getInstance();
    	if(clave!=null)
	    	if(StringUtils.isNotBlank(clave) && clave.length()<=LENGTH_KEY){
	
	    		String sql= ConfigurationManager.getProperty("select.idOrcidByCodigo");
	    		DatosPersona datosPersona = null;
	    		try {
	    			datosPersona = db.getDatosPersona(sql, clave);
	    		} catch (SQLException e) {
	    			log.warn("ERROR Obteniendo Id de Orcid:"+e.getMessage(),e);
	    			return "";
	    		}
	    		if(datosPersona != null){
	    			idsCache.put(clave, datosPersona);
	    			return datosPersona.orcid;
	    		}
	    	}
    	idsCache.put(clave,db.new DatosPersona("","","","", "", "", ""));
    	return "";
    }
    
    // TODO: Refactorizar acceso a bbdd
   	 
	 public static String getIdScholar(String clave) {
	    	boolean cacheDisabled= ConfigurationManager.getBooleanProperty("disableIdOrcidCache", false);	
	    	if(!cacheDisabled && idsCache.containsKey(clave)){
	    		return idsCache.get(clave).googleScholar;
	    	}
	    	DB db = DB.getInstance();
	    	if(clave!=null)
		    	if(StringUtils.isNotBlank(clave) && clave.length()<=LENGTH_KEY){
	
		    		String sql= ConfigurationManager.getProperty("select.idOrcidByCodigo");
		    		DatosPersona datosPersona = null;
		    		try {
		    			datosPersona = db.getDatosPersona(sql, clave);
		    		} catch (SQLException e) {
		    			log.warn("ERROR Obteniendo Id de Google Scholar:"+e.getMessage(),e);
		    			return "";
		    		}
		    		if(datosPersona != null){
		    			idsCache.put(clave, datosPersona);
		    			return datosPersona.googleScholar;
		    		}
		    	}
	    	idsCache.put(clave,db.new DatosPersona("","","","", "", "", ""));
	    	return "";
	  }
	 
	 public static String getIdResearcher(String clave) {
	    	boolean cacheDisabled= ConfigurationManager.getBooleanProperty("disableIdOrcidCache", false);	
	    	if(!cacheDisabled && idsCache.containsKey(clave)){
	    		return idsCache.get(clave).researcherID;
	    	}
	    	DB db = DB.getInstance();
	    	if(clave!=null)
		    	if(StringUtils.isNotBlank(clave) && clave.length()<=LENGTH_KEY){
	
		    		String sql= ConfigurationManager.getProperty("select.idOrcidByCodigo");
		    		DatosPersona datosPersona = null;
		    		try {
		    			datosPersona = db.getDatosPersona(sql, clave);
		    		} catch (SQLException e) {
		    			log.warn("ERROR Obteniendo Id de Researcher:"+e.getMessage(),e);
		    			return "";
		    		}
		    		if(datosPersona != null){
		    			idsCache.put(clave, datosPersona);
		    			return datosPersona.researcherID;
		    		}
		    	}
	    	idsCache.put(clave,db.new DatosPersona("","","","", "", "", ""));
	    	return "";
	  }
	 
	 public static String getIdScopus(String clave) {
	    	boolean cacheDisabled= ConfigurationManager.getBooleanProperty("disableIdOrcidCache", false);	
	    	if(!cacheDisabled && idsCache.containsKey(clave)){
	    		return idsCache.get(clave).scopusID;
	    	}
	    	DB db = DB.getInstance();
	    	if(clave!=null)
		    	if(StringUtils.isNotBlank(clave) && clave.length()<=LENGTH_KEY){
	
		    		String sql= ConfigurationManager.getProperty("select.idOrcidByCodigo");
		    		DatosPersona datosPersona = null;
		    		try {
		    			datosPersona = db.getDatosPersona(sql, clave);
		    		} catch (SQLException e) {
		    			log.warn("ERROR Obteniendo Id de Scopus:"+e.getMessage(),e);
		    			return "";
		    		}
		    		if(datosPersona != null){
		    			idsCache.put(clave, datosPersona);
		    			return datosPersona.scopusID;
		    		}
		    	}
	    	idsCache.put(clave,db.new DatosPersona("","","","", "", "", ""));
	    	return "";
	  }
	 
	 public static String getCvlac(String clave) {
	    	boolean cacheDisabled= ConfigurationManager.getBooleanProperty("disableIdOrcidCache", false);	
	    	if(!cacheDisabled && idsCache.containsKey(clave)){
	    		return idsCache.get(clave).cvlac;
	    	}
	    	DB db = DB.getInstance();
	    	if(clave!=null)
		    	if(StringUtils.isNotBlank(clave) && clave.length()<=LENGTH_KEY){
	
		    		String sql= ConfigurationManager.getProperty("select.idOrcidByCodigo");
		    		DatosPersona datosPersona = null;
		    		try {
		    			datosPersona = db.getDatosPersona(sql, clave);
		    		} catch (SQLException e) {
		    			log.warn("ERROR Obteniendo Id de Dialnet:"+e.getMessage(),e);
		    			return "";
		    		}
		    		if(datosPersona != null){
		    			idsCache.put(clave, datosPersona);
		    			return datosPersona.cvlac;
		    		}
		    	}
	    	idsCache.put(clave,db.new DatosPersona("","","","", "", "", ""));
	    	return "";
	  }
	 
	 public static String getPlumx(String clave) {
	    	boolean cacheDisabled= ConfigurationManager.getBooleanProperty("disableIdOrcidCache", false);	
	    	if(!cacheDisabled && idsCache.containsKey(clave)){
	    		return idsCache.get(clave).plumx;
	    	}
	    	DB db = DB.getInstance();
	    	if(clave!=null)
		    	if(StringUtils.isNotBlank(clave) && clave.length()<=LENGTH_KEY){
	
		    		String sql= ConfigurationManager.getProperty("select.idOrcidByCodigo");
		    		DatosPersona datosPersona = null;
		    		try {
		    			datosPersona = db.getDatosPersona(sql, clave);
		    		} catch (SQLException e) {
		    			log.warn("ERROR Obteniendo Id de Dialnet:"+e.getMessage(),e);
		    			return "";
		    		}
		    		if(datosPersona != null){
		    			idsCache.put(clave, datosPersona);
		    			return datosPersona.plumx;
		    		}
		    	}
	    	idsCache.put(clave,db.new DatosPersona("","","","", "", "", ""));
	    	return "";
	  }
	 
	 public static String getPivot(String clave) {
	    	boolean cacheDisabled= ConfigurationManager.getBooleanProperty("disableIdOrcidCache", false);	
	    	if(!cacheDisabled && idsCache.containsKey(clave)){
	    		return idsCache.get(clave).pivot;
	    	}
	    	DB db = DB.getInstance();
	    	if(clave!=null)
		    	if(StringUtils.isNotBlank(clave) && clave.length()<=LENGTH_KEY){
	
		    		String sql= ConfigurationManager.getProperty("select.idOrcidByCodigo");
		    		DatosPersona datosPersona = null;
		    		try {
		    			datosPersona = db.getDatosPersona(sql, clave);
		    		} catch (SQLException e) {
		    			log.warn("ERROR Obteniendo Id de Dialnet:"+e.getMessage(),e);
		    			return "";
		    		}
		    		if(datosPersona != null){
		    			idsCache.put(clave, datosPersona);
		    			return datosPersona.pivot;
		    		}
		    	}
	    	idsCache.put(clave,db.new DatosPersona("","","","", "", "", ""));
	    	return "";
	  }
	 
}