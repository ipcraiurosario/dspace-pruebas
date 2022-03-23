/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.bibliography.export;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dspace.content.DCDate;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.Utils;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

/**
 * Class that generates the content of a BibTeX file of an item or collection using the stored information 
 * in its the metadata. The correspondence between metadata and BibTex tags can be configured in configuration file 
 * map-bibtex.cfg
 *  
 * @author Marta Rodriguez
 *
 */
public class BibtexExporter {
	protected static ConfigurationService configService = DSpaceServicesFactory.getInstance().getConfigurationService();
	private static  String schema = configService.getProperty("BIBTEX.schema");
	private static final String BIBTEX_HEAD = "@";
	private static final String BIBTEX_OPEN="{";
	private static final String BIBTEX_CLOSE = "}";
	private static final String SEPARATOR = ":";
	private static final List<String> DATA_TYPES= Arrays.asList("text", "date");
/**
 * Extracts the information of an item to generate a String that contains the BibTeX information.
 * @param handle: Handle of the item.
 * @param context: current context.
 * @param local: current Locale.
 * @return
 */
    public static String renderBibtexFormat(String handle, Context context) {
		try {
			DSpaceObject dso = HandleServiceFactory.getInstance().getHandleService().resolveToObject(context, handle);
		 	    
			if (dso.getType() == Constants.ITEM) {
		 	    	StringBuffer sb = new StringBuffer();
		 	    	Item item = (Item) dso;
		 	    	
		 	    	//Get all metadata:
		 	    	ItemService is = item.getItemService();
		 	    	
		 	    	//Look for the type of document:
				    List<MetadataValue> types = is.getMetadata(item, schema, "type", Item.ANY, Item.ANY);
	 			    sb.append(BIBTEX_HEAD + getType(types) + BIBTEX_OPEN);
	 			    
	 			    // Citation key -> handle
	 			    sb.append(handle + ",\n");
	 			    

				    List<MetadataValue> allMetadata=is.getMetadata(item, schema, Item.ANY, Item.ANY, Item.ANY);
				    //List of used tags:
	 			    List<String> usedtags=new ArrayList<String>();
		 			
	 			    Iterator<MetadataValue> metadataIterator = allMetadata.iterator();
	 			    while (metadataIterator.hasNext()){
	 			    	MetadataValue metadata = metadataIterator.next();
		 				String field = schema + "." + metadata.getMetadataField().getElement();
		 				String qualifier = metadata.getMetadataField().getQualifier();
		 				if (qualifier != null)
		 					field = field + "." + qualifier;
	 				  
		 				//Check if a specific metadata for this type exists. If it doesn't, use the generic:
		 				String value = configService.getProperty("BIBTEX." + field);
		 				if (value!=null && !value.isEmpty()){
		 					String bibtexTag=value;
	 			    		if (value.indexOf(SEPARATOR)!=-1){
	 			    			bibtexTag=value.substring(0, value.indexOf(SEPARATOR));
	 			    			value = value.substring(value.indexOf(SEPARATOR)+1);
	 			    		}
	 			    		List<String> tags = Arrays.asList(value.split(SEPARATOR));
	 			    		Boolean single=tags.contains("single");
	 			    		
	 			    		if ((single && !usedtags.contains(bibtexTag))){
	 			    			sb.append(processData(bibtexTag, metadata.getValue(),tags));
	 	    					usedtags.add(bibtexTag);
	 			    		}else if (!single){
	 			    			sb.append(processData(bibtexTag, metadata.getValue(),tags));
	 			    		}
		 				}
	 			    }
	 				sb.append(BIBTEX_CLOSE);
	 				String bibtexData = sb.toString();
		 			return bibtexData;
	 			}
		} catch (IllegalStateException | SQLException e){
			//TODO 
		} 
		return null;
    }

	private static String getType(List<MetadataValue> types) {
		String bibtexType="";
		    boolean typeFound = false;
		    
		    Iterator<MetadataValue> typeIterator = types.iterator();
		    while (typeIterator.hasNext() && !typeFound){
		    	String type = typeIterator.next().getValue();
		    	
		    	// Look if there's a type in the type map
				if (type!=null && !type.isEmpty()){
					String prefix = configService.getProperty("BIBTEX.type.prefix");
					if (prefix!=null){
						try{
							type=type.substring(prefix.length());
						}catch (IndexOutOfBoundsException e){}
					}
					bibtexType=configService.getProperty("BIBTEX.type."+type.replaceAll("\\s", "").toLowerCase());
					if (bibtexType!=null && !bibtexType.isEmpty())
						typeFound = true;
				}
		    }
		    
		    // Set type in case no type is given
		    if (!typeFound) {
		    	bibtexType = configService.getProperty("BIBTEX.type.default");
		    }
		    return bibtexType;
	}

	/**
	 * Function that processes the introduced value depending on the type of data specified in the tags.
	 * @param bibtexTag: BibTeX tag.
	 * @param value: Value of the metadata.
	 * @param dataTypes: List that contains the data types associated to this value.
	 */
	private static String processData(String bibtexTag, String value, List<String> dataTypes) {
		String outData=bibtexTag+" = {"+value+"},\n";
		Iterator<String> it =dataTypes.iterator();
		while(it.hasNext()){
			String tag= it.next();
			if (DATA_TYPES.contains(tag)){
				if (tag.equals("date")){
					DCDate dcDate = new DCDate(value);
					int month = dcDate.getMonth();
					int year=dcDate.getYear();
					outData="";
					
					if (year!=-1){
						outData = outData + "year = {"+Integer.toString(year)+"},\n";
					}						
					if (month>=1 && month<=12){
						outData = outData + "month = {"+Integer.toString(month) +"},\n";
					}
				} else{
					outData=Utils.addEntities(outData);
				}
			}
		}
		return outData;
	}
}