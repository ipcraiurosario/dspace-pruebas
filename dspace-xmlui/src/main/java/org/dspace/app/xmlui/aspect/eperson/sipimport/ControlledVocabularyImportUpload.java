/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.eperson.sipimport;

import java.sql.SQLException;

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
import org.dspace.storage.rdbms.DB;
import org.xml.sax.SAXException;

/**
 * Web interface to Bulk Sip Import app.
 *
 * Display form for user to view errors and confirm
 *
 * @author AdĂĄn RomĂĄn Ruiz at arvo.es
 */

public class ControlledVocabularyImportUpload extends AbstractDSpaceTransformer {

	/** Language strings */
	private static final Message T_dspace_home = message("xmlui.general.dspace_home");
	private static final Message T_submit_return = message("xmlui.general.return");
	private static final Message T_trail = message("xmlui.administrative.cvimport.general.trail");
	private static final Message T_title = message("xmlui.administrative.cvimport.general.title");
	private static final Message T_head1 = message("xmlui.administrative.cvimport.general.head1");

	private static final Message T_para = message("xmlui.administrative.cvimport.SipImportUpload.hint");
	private static final Message T_para_error= message("xmlui.administrative.cvimport.SipImportUpload.hint.error");
    private static final Message T_submit_confirm = message("xmlui.administrative.cvimport.SipImportUpload.submit_confirm");
    private static final Message T_table_exist = new Message("default", "xmlui.administrative.controlled.vocabulary.import.flow.table_exist");
    private static final Message T_table_not_exist = new Message("default", "xmlui.administrative.controlled.vocabulary.import.flow.table_new");

	public void addPageMeta(PageMeta pageMeta) throws WingException  
	{
		pageMeta.addMetadata("title").addContent(T_title);
		
		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrail().addContent(T_trail);
	}

	
	public void addBody(Body body) throws SAXException, WingException, SQLException
	{
		// Get list of changes

	Request request = ObjectModelHelper.getRequest(objectModel);
	String outcome = parameters.getParameter("outcome","");
	String errors = parameters.getParameter("errors",null);
	
	// DIVISION: metadata-import
	Division div = body.addInteractiveDivision("controlled-vocabulary-import",contextPath + "/controlledvocabularyimport", Division.METHOD_MULTIPART,"primary administrative");
	div.setHead(T_head1);
	String nombreTabla = (String)request.getSession().getAttribute("nombreTabla");
	boolean exist=DB.getInstance().existTable(nombreTabla);
	
	if(!"failure".equalsIgnoreCase(outcome)){
	    div.addPara(T_para);
	    
		if (exist){
		    div.addPara(T_table_exist.parameterize(nombreTabla));
		}else{
		    div.addPara(T_table_not_exist.parameterize(nombreTabla));
		}
	}else{
	    div.addPara(T_para_error);
	    div.addPara(errors);
	}
       // div.addPara(mapfile);

        Para actions = div.addPara();
        
        if(!"failure".equalsIgnoreCase(outcome) && exist){
            Button applychanges = actions.addButton("submit_confirm");
            applychanges.setValue(T_submit_confirm);
	}
        Button cancel = actions.addButton("submit_return");
        cancel.setValue(T_submit_return);
  
        div.addHidden("administrative-continue").setValue(knot.getId());
	}

}