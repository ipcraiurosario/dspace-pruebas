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

import javax.swing.text.TableView;

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
import org.xml.sax.SAXException;

/**
 * Web interface to Bulk Sip Import app.
 *
 * Initial select file / upload ZIP form
 *
 * @author AdĂĄn RomĂĄn Ruiz at arvo.es
 */

public class ControlledVocabularyImportMain extends AbstractDSpaceTransformer {

	/** Language strings */
	private static final Message T_dspace_home = message("xmlui.general.dspace_home");

	private static final Message T_title = message("xmlui.administrative.cvimport.general.title");
	private static final Message T_head1 = message("xmlui.administrative.cvimport.general.head1");
        private static final Message T_submit_upload = message("xmlui.administrative.cvimport.CVImportMain.submit_upload");
        private static final Message T_trail = message("xmlui.administrative.cvimport.general.trail");
        private static final Message T_para_subida = message("xmlui.administrative.cvimport.body.subida");
   
        private static Logger log = Logger.getLogger(ControlledVocabularyImportMain.class);
        
	public void addPageMeta(PageMeta pageMeta) throws WingException  
	{
		pageMeta.addMetadata("title").addContent(T_title);
		
		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrail().addContent(T_trail);
	}

	
	public void addBody(Body body) throws SAXException, WingException, SQLException
	{

		// DIVISION: metadata-import
		Division div = body.addInteractiveDivision("controlled-vocabulary-import",contextPath + "/controlledvocabularyimport", Division.METHOD_MULTIPART,"primary administrative");
		div.setHead(T_head1);
		
		div.addPara(T_para_subida);
                Para file = div.addPara();
                file.addFile("file");

                Para actions = div.addPara();
                Button button = actions.addButton("submit_upload");
                button.setValue(T_submit_upload);
                
		div.addHidden("administrative-continue").setValue(knot.getId());
	}
}