/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.eperson.sipimport;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.cocoon.environment.Request;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Button;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Para;
import org.dspace.app.xmlui.wing.element.Select;
import org.dspace.core.ConfigurationManager;
import org.dspace.storage.rdbms.DB;
import org.xml.sax.SAXException;

/**
 * Web interface to Bulk Sip Import app.
 *
 * Initial select file / upload ZIP form
 *
 * @author AdĂĄn RomĂĄn Ruiz at arvo.es
 */

public class ControlledVocabularyExportMain extends AbstractDSpaceTransformer {

	/** Language strings */
	private static final Message T_dspace_home = message("xmlui.general.dspace_home");

	private static final Message T_title = message("xmlui.administrative.cvexport.general.title");
	private static final Message T_head1 = message("xmlui.administrative.cvexport.general.head1");
        private static final Message T_trail = message("xmlui.administrative.cvexport.general.trail");
        private static final Message T_para_exportacion = message("xmlui.administrative.cvexport.body.exportacion");
        private static final Message T_para_seleccion = message("xmlui.administrative.cvexport.body.seleccion");
        private static final Message T_submit = message("xmlui.administrative.cvexport.submit.existente");
        private static Logger log = Logger.getLogger(ControlledVocabularyExportMain.class);
        
	public void addPageMeta(PageMeta pageMeta) throws WingException  
	{
		pageMeta.addMetadata("title").addContent(T_title);
		
		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrail().addContent(T_trail);
	}

	
	public void addBody(Body body) throws SAXException, WingException, SQLException
	{

		// DIVISION: metadata-import
	    	Division div = body.addInteractiveDivision("controlled-vocabulary-export",contextPath + "/controlledvocabularyexport", Division.METHOD_MULTIPART,"primary administrative");
		div.setHead(T_head1);
		
		div.addPara(T_para_exportacion);
              
                Para actions = div.addPara();
               
                    div.addPara(T_para_seleccion);
                    Para selector=div.addPara();
                    Select select=selector.addSelect("select");
                    populateSelect(select);
                    actions = div.addPara();
                    Button button = actions.addButton("submit");
                    button.setValue(T_submit);

		div.addHidden("administrative-continue").setValue(knot.getId());
		
	}



	// Rellena el select con los ficheros del directorio ftp de ficheros de importacion SIP
	private void populateSelect(Select select) throws WingException {
	    ArrayList<String> tablenames=DB.getInstance().getTableNames();
	    for(int i=0;i<tablenames.size();i++){
		    select.addOption(tablenames.get(i),tablenames.get(i));
		}
	    }

}