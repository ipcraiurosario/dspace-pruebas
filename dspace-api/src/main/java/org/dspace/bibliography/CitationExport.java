package org.dspace.bibliography;

import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Iterator;
import java.util.List;

import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

/**
 * 
 * @author Sergio LÃ³pez at arvo.es
 *
 */
public class CitationExport {
	protected static ConfigurationService configService = DSpaceServicesFactory.getInstance().getConfigurationService();
	private static String schema = configService.getProperty("APA.schema");
	private static final int CHARLIMIT=50;
    
	/**
	 * Genera la cita en formato APA
	 * @param handle Handle del item
	 * @param context Contexto actual
	 * @return String Cadena que contiene la cita en formato APA
	 */
    public static String renderCitaFormat(String handle, Context context){
    	try {
			DSpaceObject dso = HandleServiceFactory.getInstance().getHandleService().resolveToObject(context, handle);

	 	    if (dso.getType() == Constants.ITEM) {
	 	    	Item item = (Item) dso;
	 	    	ItemService is = item.getItemService();
	 	    	
	 	    	String citaType = getType(item);

	 	    	String cita = configService.getProperty("APA."+citaType.toLowerCase());
	 	    	if(cita != null && !cita.equalsIgnoreCase("")){
	 	    		String acumulador = "";
	 	    		List<String> params = configService.getPropertyKeys("APA."+citaType.toLowerCase()+".");
	 	    		
    				// El primer elemento de la lista es el parametro que se le pasa al metodo anterior
    				// P.Ej. si se le pasa APA.book. , ese sera el primer elemento de la lista
    				// Por tanto hay que saltarlo
    				boolean primeroSaltado = false;
    				for(String param : params){
    					if(!primeroSaltado){
    						primeroSaltado = true;
    						continue;
    					}
    					String patternData = param.split("\\.")[param.split("\\.").length-1];
    					String formato = configService.getProperty("APA.pattern.type." + patternData);
    					String[] metadatosConfigurados = configService.getArrayProperty(param);
    					int nMetsConf = 0;
    					for(String met : metadatosConfigurados){
    						List<MetadataValue> itemMetadata = is.getMetadataByMetadataString(item, met);
    						int nMetadataValue = 0;
    						for(MetadataValue metadataValue : itemMetadata){
    							acumulador += processData(metadataValue.getValue(), formato);
    							if(itemMetadata.size() > 1){
    								if(nMetadataValue == (itemMetadata.size()-2))
    									acumulador += " & ";
    								else
    									acumulador += ", ";
    							}
    							nMetadataValue++;
    						}
    						if(metadatosConfigurados.length > 1)
    							if(nMetsConf < metadatosConfigurados.length-1)
    								acumulador += ", ";
    						nMetsConf++;
    					}
        				cita = cita.replace("$"+patternData, acumulador);
        				acumulador = "";
    				}
	 	    	}
	 	    	return cita;
	 	    }
		} catch (IllegalStateException | SQLException e) {
			return null;
		}
		return null;
    }

	private static String processData(String metadato, String formato) {
		String dato = "";
		
		switch(formato){
			case"text":
				dato= Normalizer.normalize(metadato, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
				break;
			case"year":
				dato = metadato.substring(0,4);
				break;
			case"charlimit50":
				// Arvo: Rosario no quiere que se acorten los titulos
				//if (metadato.length()>CHARLIMIT-3){
				//	dato=metadato.substring(0,CHARLIMIT-3);
				//	int spaceIndex=dato.lastIndexOf(" ");
				//	if (spaceIndex!=-1)
				//		dato=metadato.substring(0, spaceIndex);
				//	dato = dato+"...";
				//}
				//else
					dato = metadato;
				break;
			case"int":
				dato = formato;
				break;
		}
		
		return dato.toString();
	}
/**
 * Gets the RIS type of this item. In case there is not an equivalent, returns the default type set on 
 * the configuration file.
 * @param item
 * @return RIS type
 */
	private static String getType(Item item) {
		String citaType="";
		boolean typeFound = false;
		    
		    ItemService is = item.getItemService();
		    List<MetadataValue> types=is.getMetadataByMetadataString(item, schema+".type");
		    Iterator<MetadataValue> it = types.iterator();
		    
		    while(it.hasNext()){
		    	MetadataValue mv = it.next();
		    	String value = mv.getValue();
		    	// Look if there's a type in the type map
				if (value!=null && !value.isEmpty()){
					// Look if there's a type in the type map
					String prefix= configService.getProperty("APA.type.prefix");
					if (prefix!=null){
						try{
							value=value.substring(prefix.length());
						}catch (IndexOutOfBoundsException e){}
					}
					citaType=configService.getProperty("APA.type."+value.replaceAll("\\s", "").toLowerCase());
					if (citaType!=null && !citaType.isEmpty()){
						typeFound = true;
					}
				}
		    }
		    // Set type in case no type is given
		    if (!typeFound) {
		    	citaType=configService.getProperty("APA.default.type");
		    }
		    return citaType;
	}
}