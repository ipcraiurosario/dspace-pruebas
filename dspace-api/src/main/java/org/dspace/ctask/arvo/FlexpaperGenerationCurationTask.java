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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Constants;
import org.dspace.curate.AbstractCurationTask;
import org.dspace.curate.Curator;
import org.dspace.curate.Suspendable;

import es.arvo.app.mediafilter.FlexpaperUtils;


/**
 * Genera los flexpaper.
 * 
 * @author AdÃ¡n RomÃ¡n Ruiz
 */
@SuppressWarnings("deprecation")
@Suspendable
public class FlexpaperGenerationCurationTask extends AbstractCurationTask
{

	// map of required fields
	private Map<String, List<String>> reqMap = new HashMap<String, List<String>>();

	@Override 
	public void init(Curator curator, String taskId) throws IOException
	{
		super.init(curator, taskId);

	}

	/**
	 * Perform the curation task upon passed DSO
	 *
	 * @param dso the DSpace object
	 * @throws IOException
	 */
	@Override
	public int perform(DSpaceObject dso) throws IOException
	{
		if (dso.getType() == Constants.ITEM)
		{
			Item item = (Item)dso;
//			Context context;
//			try {
//				context = new Context();
//			} catch (SQLException e1) {
//				setResult("Error obteniendo contexto");
//				return Curator.CURATE_ERROR;                
//			}
			String handle = item.getHandle();
			if (handle == null)
			{
				// we are still in workflow - no handle assigned
				setResult("El item no tiene Handle. Debe estar en un workflow. Id:"+item.getID());
				return Curator.CURATE_SKIP;   
			}
			List<Bundle> bundles;
			try {
				bundles = ContentServiceFactory.getInstance().getItemService().getBundles(item, "ORIGINAL");
			} catch (SQLException e) {
				setResult("El item no tiene Bundle de tipo \"ORIGINAL\"");
				return Curator.CURATE_SKIP;                
			}
			if(bundles.size()==1){
				Bundle bundle=bundles.get(0);
				List<Bitstream> bitstreams=bundle.getBitstreams();
				if(bitstreams.size()>0){
					for(int i=0;i<bitstreams.size();i++){
						try {
							if(FlexpaperUtils.flexpaperMustBeGenerated(handle, bitstreams.get(i).getName(), bitstreams.get(i).getSequenceID(), bitstreams.get(i))){
								FlexpaperUtils.generateFlash(handle, bitstreams.get(i).getName(), bitstreams.get(i).getSequenceID(), bitstreams.get(i));
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					setResult("Ficheros flexpaper generados para el item:"+item.getHandle());
					return Curator.CURATE_SUCCESS;
				}else{
					setResult("El item no tiene ficheros");
					return Curator.CURATE_SKIP;                
				}
			}else{
				setResult("El item tiene mas de un Bundle de tipo \"ORIGINAL\"");
				return Curator.CURATE_SKIP;                
			}
		}else{
			setResult("Solo se afectan items");
			return Curator.CURATE_SKIP;                
		}
	}
}