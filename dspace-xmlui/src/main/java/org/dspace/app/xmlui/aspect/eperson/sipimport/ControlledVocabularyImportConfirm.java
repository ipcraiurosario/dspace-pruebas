/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.eperson.sipimport;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Button;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Para;
import org.xml.sax.SAXException;
import org.dspace.app.bulkedit.BulkEditChange;

/**
 * Web interface to Bulk Sip Import app.
 *
 * Display errors
 * 
 * @author AdĂĄn RomĂĄn Ruiz at arvo.es
 */

public class ControlledVocabularyImportConfirm extends AbstractDSpaceTransformer {

	/** Language strings */
	private static final Message T_dspace_home = message("xmlui.general.dspace_home");
	private static final Message T_submit_return = message("xmlui.general.return");
	private static final Message T_trail = message("xmlui.administrative.cvimport.general.trail");
    	private static final Message T_title = message("xmlui.administrative.cvimport.general.title");
	private static final Message T_head1 = message("xmlui.administrative.cvimport.general.head1");

    private static final Message T_success = message("xmlui.administrative.cvimport.CvImportConfirm.success");

	public void addPageMeta(PageMeta pageMeta) throws WingException  
	{
		pageMeta.addMetadata("title").addContent(T_title);
		
		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrail().addContent(T_trail);
	}

	
	public void addBody(Body body) throws SAXException, WingException, SQLException
	{
	    String outcome = parameters.getParameter("outcome","");
	    String errors = parameters.getParameter("errors",null);
	    
	    Request request = ObjectModelHelper.getRequest(objectModel);

	    // DIVISION: metadata-import
	    Division div = body.addInteractiveDivision("controlled-vocabulary-import",contextPath + "/controlledvocabularyimport", Division.METHOD_MULTIPART,"primary administrative");
	    div.setHead(T_head1);

	    if("failure".equalsIgnoreCase(outcome)){
		Para para = div.addPara();
		para.addContent(errors);
	    }else{
		Para para = div.addPara();
		para.addContent(T_success);
	    }
	    Para actions = div.addPara();
	    Button cancel = actions.addButton("submit_return");
	    cancel.setValue(T_submit_return);


	    div.addHidden("administrative-continue").setValue(knot.getId());
	}
}