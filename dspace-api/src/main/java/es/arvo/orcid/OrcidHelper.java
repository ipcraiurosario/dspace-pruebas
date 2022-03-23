package es.arvo.orcid;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Email;
import org.dspace.core.I18nUtil;
import org.orcid.ns.OrcidMessage;
import org.orcid.ns.PersonalDetails;

public class OrcidHelper {

    public static String ORCIDMAIL="orcid_request";
    public static String ORCIDMAILREINTENTO="orcid_request_retry";
    public static String ORCIDMAILGESTIONMANUAL="orcid_mail_gestion_manual";
    public static String MODE_AUTENTICACION="autenticacion";
    public static String MODE_CREACION="creacion";
    // Primo para encriptar un poco el id de usuario
    public static int MAGICNUMBER=317;
    private static final Logger log = Logger.getLogger(OrcidHelper.class);
    
    private static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSSS");
    
    public static void processEmailSending() {
	String orcidMode=ConfigurationManager.getProperty("orcid.mode");

	if(StringUtils.isNotBlank(orcidMode) && MODE_CREACION.equalsIgnoreCase(orcidMode)){
	    //TODO: modo de creacion batch
	}else{
	    // Autores nuevos
	    ArrayList <OrcidVO> orcids= OrcidDataProvider.getAutoresNuevos();
	    Date ahora=new Date();
	    for(int i=0;i<orcids.size();i++){
		OrcidVO orcidVO=orcids.get(i);
		try {
        		if(StringUtils.isNotBlank(orcidVO.getEmail())){
        		    Email email=Email.getEmail(I18nUtil.getEmailFilename(Locale.getDefault(), ORCIDMAIL));
        		    email.addRecipient(orcidVO.getEmail());
        		    String url=construirUrlAceptarPermisos(orcidVO);
        		    email.addArgument(url);
        		    email.send();
        
        		    orcidVO.setFirstEmailTryDate(ahora);
        		    orcidVO.setLastEmailTryDate(ahora);
        		    OrcidDataProvider.actualizaAutor(orcidVO);
        		}
		} catch (IOException e) {
		    log.error("Error preparando mail",e);
		    e.printStackTrace();
		} catch (MessagingException e) {
		    log.error("Error enviando mail",e);
		    e.printStackTrace();
		}
	    }
	    //Reenviar a autores que no lo han rellenado
	    orcids= OrcidDataProvider.getAutoresReintentos();
	    for(int i=0;i<orcids.size();i++){
		OrcidVO orcidVO=orcids.get(i);
		try {
		    if(orcidVO.getEmail()!=null){
        		    Email email=Email.getEmail(I18nUtil.getEmailFilename(Locale.getDefault(), ORCIDMAILREINTENTO));
        		    email.addRecipient(orcidVO.getEmail());
        		    String url=construirUrlAceptarPermisos(orcidVO);
        		    email.addArgument(url);
        		    email.send();
        
        		    orcidVO.setLastEmailTryDate(ahora);
        		    OrcidDataProvider.actualizaAutor(orcidVO);
		    }
		} catch (IOException e) {
		    log.error("Error preparando mail",e);
		    e.printStackTrace();
		} catch (MessagingException e) {
		    log.error("Error enviando mail",e);
		    e.printStackTrace();
		}
	    }
	}
    }

    private static String construirUrlAceptarPermisos(OrcidVO persona) {
	   //https://sandbox.orcid.org/oauth/authorize?client_id=APP-674MCQQR985VZQ2Z&response_type=code&scope=/orcid-profile/read-limited&redirect_uri=https://developers.google.com/oauthplayground
	//   https://sandbox.orcid.org/oauth/authorize?client_id=APP-EC86TCB4725VXPSG&response_type=code&scope=/orcid-profile/read-limited&redirect_uri=http://89.128.83.119:8080/e-ieo/orcid_access
	    StringBuffer url=new StringBuffer();
	    //url.append("<a id=\"connect-orcid-link\" href=\"");
	    url.append(ConfigurationManager.getProperty("orcid.url.request"));
	    url.append("/oauth/authorize?client_id=");
	    url.append(ConfigurationManager.getProperty("orcid.client.id"));
	    if(StringUtils.isNotBlank(persona.getNombre())){
		url.append("&given_names="+URLEncoder.encode(persona.getNombre()));
	    }
	    if(StringUtils.isNotBlank(persona.getApellidos())){
		url.append("&family_names="+URLEncoder.encode(persona.getApellidos()));
	    }
	   
	    if(StringUtils.isNotBlank(persona.getOrcidId())){
		url.append("&orcid="+URLEncoder.encode(persona.getOrcidId()));
	    }else  if(StringUtils.isNotBlank(persona.getEmail())){
		url.append("&email="+URLEncoder.encode(persona.getEmail()));
	    }

	    url.append("&state="+persona.getId()*MAGICNUMBER);
	    url.append("&response_type=code&lang=es&scope=/read-limited%20/activities/update%20/person/update&redirect_uri=");
	    url.append(ConfigurationManager.getProperty("orcid.url.redireccion"));
	   // url.append("\"><img id=\"orcid-id-logo\" src=\"http://orcid.org/sites/default/files/images/orcid_16x16.png\" width='16' height='16' alt=\"ORCID logo\"/>Create or Connect your ORCID iD</a>");
	    return url.toString();
    }
    
   // 'client_id=APP-674MCQQR985VZQ2Z&client_secret=5f63d1c5-3f00-4fa5-b096-fd985ffd0df7&grant_type=authorization_code&code=Q70Y3A&redirect_uri=https://developers.google.com/oauthplayground'
    public static String construirDataUrlToken(String code) {
    	StringBuffer url=new StringBuffer();
    	url.append("client_id="+ConfigurationManager.getProperty("orcid.client.id"));
    	url.append("&client_secret="+ConfigurationManager.getProperty("orcid.secret"));
    	url.append("&grant_type=authorization_code");
    	url.append("&code="+code);
    	url.append("&redirect_uri="+ConfigurationManager.getProperty("orcid.url.redireccion"));	
    	return url.toString();
    }

    /**
     * State se lo mandamos en la peticion y nos lo devuelve para identificar al autor. Dividir por MAGICNUMBER (para evitar que lo toquen a mano y nos pisen el dato de otro.
     * @param personalDetails
     * @param state
     * @return
     */
    public static OrcidVO buscarPersona(PersonalDetails personalDetails,String state) {
	if(StringUtils.isNotBlank(state)){
	    try {
		int idMagic=Integer.parseInt(state);
		if(idMagic%MAGICNUMBER==0){
		    int id=idMagic/MAGICNUMBER;
		    return OrcidDataProvider.buscarPersona(id);
		}
	    } catch (NumberFormatException e) {/* No pasa nada*/}
	}
	return OrcidDataProvider.buscarPersona(personalDetails);
    }

    public static void actualizaAutor(OrcidVO orcidVO) {
	OrcidDataProvider.actualizaAutor(orcidVO);
	
    }
    /*Orcid:{0}
    Nombre de Orcid:{1}
    Token:{2}
    TokenType:{3}   
    TokenExpiration:{4}
    FechaAceptacion:{5}*/    
    public static void mandarCorreoGestionManual(OrcidAccessResponseVO orcidAccessResponse, OrcidMessage profile) throws IOException, MessagingException {
	Email email=Email.getEmail(I18nUtil.getEmailFilename(Locale.getDefault(), ORCIDMAILGESTIONMANUAL));
	    email.addRecipient(ConfigurationManager.getProperty("orcid.mail.admin"));
	    email.addArgument(orcidAccessResponse.getOrcid());
	    email.addArgument(orcidAccessResponse.getName());
	    email.addArgument(orcidAccessResponse.getAccessToken());
	    email.addArgument(orcidAccessResponse.getTokenType());
	    email.addArgument(orcidAccessResponse.getExpiresIn());
	    email.addArgument(new Date());
	    email.send();
    }

}
