package es.arvo.orcid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.dspace.core.ConfigurationManager;
import org.dspace.storage.rdbms.DB;
import org.orcid.ns.PersonalDetails;

//TODO: Es un mock, completar.
public class OrcidDataProvider {
    private static Logger log = Logger.getLogger(OrcidDataProvider.class);
    
    public static ArrayList<OrcidVO> getAutoresNuevos() {
    	ResultSet rs=null;
	try {
		DB db=DB.getInstance();
		Connection con=db.getAutoritiesConection();
		String query=ConfigurationManager.getProperty("orcid.query.autores.nuevos");
		PreparedStatement ps;
		 
	    ps = con.prepareStatement(query);
	    rs=ps.executeQuery();
	} catch (SQLException e) {
	    log.error("Error ejecutando la consulta de lista de personas en orcid",e);
	}
	
	if (rs!=null){
	    return rsToOrcidVOList(rs);
	}
	return new ArrayList<OrcidVO>();
    }

    private static ArrayList<OrcidVO> rsToOrcidVOList(ResultSet rs) {
	ArrayList<OrcidVO> list=new ArrayList<OrcidVO>();
	if(rs!=null){
	    try {
		OrcidVO orcidvo=null;
		while (rs.next()) {
        		orcidvo=rsToOrcidVO(rs);
        		if(orcidvo!=null){
        		    list.add(orcidvo);
        		}
		}
	    } catch (SQLException e) {
		log.error("Error convirtiendo la lista de personas en orcid",e);
	    }
	}
	return list;
    }
    private static OrcidVO rsToOrcidVO(ResultSet rs) {
	try {
	    if(rs!=null && !rs.isAfterLast()){
	        OrcidVO orcidvo=new OrcidVO();
	        orcidvo.setId(rs.getInt("id"));
	        orcidvo.setAccessToken(rs.getString("access_token"));
	        orcidvo.setAceptationDate(rs.getDate("aceptation_date"));
	        orcidvo.setApellidos(rs.getString("apellidos"));
	        orcidvo.setBaja(rs.getBoolean("baja"));
	        orcidvo.setEmail(rs.getString("email"));
	        orcidvo.setFirstEmailTryDate(rs.getDate("first_email_try_date"));
	        orcidvo.setLastEmailTryDate(rs.getDate("last_email_try_date"));
	        orcidvo.setNombre(rs.getString("nombre"));
	        orcidvo.setOrcidId(rs.getString("orcid"));
	        orcidvo.setOrcidName(rs.getString("orcid_name"));
	        orcidvo.setTokenExpiration(rs.getInt("token_expiration"));
	        orcidvo.setTokenType(rs.getString("token_type"));
	        orcidvo.resetChanges();
	        return orcidvo;
	    }
	} catch (SQLException e) {
	    log.error("Error convirtiendo la persona en orcid",e);
	}
	return null;
    }

/**
 * Actualiza un autor en la bbdd.
 * @param orcidVO
 * @return true si se actualizo. False si no habia cambios
 */
    public static boolean actualizaAutor(OrcidVO orcidVO) {
	if(orcidVO.hasChanged()){
        	try {
        		DB db=DB.getInstance();
            	Connection con=db.getAutoritiesConection();
            	PreparedStatement ps;
            	StringBuffer sb=new StringBuffer();
            	sb.append(" update vc_persona set ");
            	sb.append(orcidVO.getBBDDSetString());
            	sb.append(" where id=?");
        	    ps = con.prepareStatement(sb.toString());
        	    ps.setInt(1,orcidVO.getId());
        	    int numRegistros=ps.executeUpdate();
        	    if(numRegistros!=1){
        		 log.warn("Ha fallado la actualizacion de un registro de persona.");
        		 return false;
        	    }
        	    return true;
        	} catch (SQLException e) {
        	    log.error("Error ejecutando la consulta de lista de personas en orcid",e);
        	    return false;
        	}
	}else{
	    return false;
	}
    }

    public static ArrayList<OrcidVO> getAutoresReintentos() {
    	ResultSet rs=null;
	try {
		DB db=DB.getInstance();
		Connection con=db.getAutoritiesConection();
		String query=ConfigurationManager.getProperty("orcid.query.autores.reintento");
		String diasReintento=ConfigurationManager.getProperty("orcid.mail.request.dias.reintento")==null?"10":ConfigurationManager.getProperty("orcid.mail.request.dias.reintento");
		PreparedStatement ps;
		
	    ps = con.prepareStatement(query);
	    ps.setInt(1, Integer.parseInt(diasReintento));
	    rs=ps.executeQuery();
	} catch (SQLException e) {
	    log.error("Error ejecutando la consulta de lista de personas en orcid",e);
	}
	
	if (rs!=null){
	    return rsToOrcidVOList(rs);
	}
	return new ArrayList<OrcidVO>();
    }

    /**
     * Mejorar algoritmo de busqueda de autoridades
     * @param personalDetails
     * @return
     */
    public static OrcidVO buscarPersona(PersonalDetails personalDetails) {
	if(personalDetails!=null){
		ResultSet rs=null;
        	try {
        		DB db=DB.getInstance();
            	Connection con=db.getAutoritiesConection();
            	String query=ConfigurationManager.getProperty("orcid.query.autor.buscar");
            	PreparedStatement ps;
            	
        	    ps = con.prepareStatement(query);
        	    ps.setString(1, personalDetails.getGivenNames());
        	    ps.setString(2, personalDetails.getFamilyName());
        	    rs=ps.executeQuery();
        	} catch (SQLException e) {
        	    log.error("Error ejecutando la consulta de lista de personas en orcid",e);
        	}
        
        	if (rs!=null){
        	    ArrayList<OrcidVO> list=rsToOrcidVOList(rs);
        	    if(list.size()==1){
        		return list.get(0);
        	    }
        
        	}
	}
	return null;
    }
    public static OrcidVO buscarPersona(int id) {
    ResultSet rs=null;
   	try {
   		DB db=DB.getInstance();
   	   	Connection con=db.getAutoritiesConection();
   	   	String query=ConfigurationManager.getProperty("orcid.query.autor.getById");
   	   	PreparedStatement ps;
   	   	
   	    ps = con.prepareStatement(query);
   	    ps.setInt(1, id);
   	    rs=ps.executeQuery();
   	} catch (SQLException e) {
   	    log.error("Error ejecutando la consulta de lista de personas en orcid",e);
   	}

   	if (rs!=null){
   	    ArrayList<OrcidVO> list=rsToOrcidVOList(rs);
   	    if(list.size()==1){
   		return list.get(0);
   	    }

   	}
   	return null;
       }
}
