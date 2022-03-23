/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.ctask.arvo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.authority.Choices;
import org.dspace.core.Context;
import org.dspace.curate.AbstractCurationTask;
import org.dspace.curate.Curator;
import org.dspace.curate.Curator.TxScope;

/**
 * @author Sergio Nieto CaramÃŠs snieto AT arvo.es
 * Valida los metadatos obtenidos en dc.contributor.author y dc.contributor.editor
 * 
 * MEJORAS: hacer que coja esos valores de un fichero
 */

public class ValidateAuthor extends AbstractCurationTask {

	private static Logger log = Logger.getLogger(ValidateAuthor.class);

	private static int MIN_CONFIDENCE = Choices.CF_ACCEPTED; 

	@Override
	public void init(Curator curator, String taskId) throws IOException {
		super.init(curator, taskId);
		curator.setTransactionScope(TxScope.OBJECT);
	}

	@Override
	public int perform(DSpaceObject dso) throws IOException {
		if (!(dso instanceof Item)) {
			return Curator.CURATE_SKIP;
		}
		Item item=null;
		Context context=null;
		try {
		    context = Curator.curationContext();
		} catch (SQLException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		try {
//        		context=new Context();
//        		context.turnOffAuthorisationSystem();
        		
        		item = (Item) dso;

		
			validate(context,item,"dc.creator");
			validate(context,item,"dc.contributor.advisor");
			
			item.getItemService().update(context, item);
			
			setResult("Enviado");
			report("Metadatos actualizados para el item: " + item.getID());
			
			} catch (SQLException e) {
			// TODO: handle exception
				error(e,item);
        		} catch (AuthorizeException e) {
        			error(e,item);
        			return Curator.CURATE_ERROR;
        		}finally {
//        		    try {
//        			if(context!=null){
//        			    context.commit();
//        			    context.reloadEntity(item);
////        			    context.restoreAuthSystemState();
////        			    context.complete();
//        			}
//			    } catch (SQLException e) {
//				e.printStackTrace();
//			    }
        		}
		
		return Curator.CURATE_SUCCESS;

	}
	
	/* Validamos los metadatos del item si ya no esta validado el item */
	private void validate(Context context,Item item, String metadata) throws SQLException, AuthorizeException {
		List<MetadataValue> dcValues = item.getItemService().getMetadataByMetadataString(item, metadata);
		String [] token =metadata.split("\\.");
		if(token.length >= 3)
			item.getItemService().clearMetadata(context, item, token[0], token[1], token[2], Item.ANY);
		else
			item.getItemService().clearMetadata(context, item, token[0], token[1], null, Item.ANY);
		for (MetadataValue dcValue: dcValues){
			if(dcValue.getConfidence()>=MIN_CONFIDENCE)
				item.getItemService().addMetadata(
						context, 
						item, 
						dcValue.getMetadataField().getMetadataSchema().getName(), 
						dcValue.getMetadataField().getElement(), 
						dcValue.getMetadataField().getQualifier(), 
						dcValue.getLanguage(), 
						dcValue.getValue(), 
						dcValue.getAuthority(), 
						dcValue.getConfidence());
			else
				item.getItemService().addMetadata(
						context, 
						item, 
						dcValue.getMetadataField().getMetadataSchema().getName(), 
						dcValue.getMetadataField().getElement(), 
						dcValue.getMetadataField().getQualifier(), 
						dcValue.getLanguage(), 
						dcValue.getValue());
		}
	}
	


	
	
	private int error(Exception e,Item item){
		String message = "Error BBDD=" + item.getID()
				+ " con mime : " + e.getMessage();
		log.error(message, e);
		report(message);
		setResult(message);
		return Curator.CURATE_ERROR;
	}

	

}