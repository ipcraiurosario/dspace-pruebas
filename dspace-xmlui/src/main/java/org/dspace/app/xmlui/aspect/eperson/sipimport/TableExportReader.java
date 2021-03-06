/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.eperson.sipimport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.cocoon.reading.AbstractReader;
import org.apache.log4j.Logger;
import org.dspace.app.bulkedit.MetadataExport;
import org.dspace.app.tablecsv.DSpaceTableCSV;
import org.dspace.app.xmlui.utils.AuthenticationUtil;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.xml.sax.SAXException;

/**
 *
 * AbstractReader that generates a CSV of item, collection
 * or community metadata using MetadataExport
 *
 * @author Kim Shepherd
 */

public class TableExportReader extends AbstractReader implements Recyclable
{

     /**
     * Messages to be sent when the user is not authorized to view 
     * a particular bitstream. They will be redirected to the login
     * where this message will be displayed.
     */
	private static final String AUTH_REQUIRED_HEADER = "xmlui.ItemExportDownloadReader.auth_header";
	private static final String AUTH_REQUIRED_MESSAGE = "xmlui.ItemExportDownloadReader.auth_message";
	
	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HHmmss-SSSS");
    /**
     * How big a buffer should we use when reading from the bitstream before
     * writing to the HTTP response?
     */
    protected static final int BUFFER_SIZE = 8192;

    /**
     * When should a download expire in milliseconds. This should be set to
     * some low value just to prevent someone hitting DSpace repeatedly from
     * killing the server. Note: there are 60000 milliseconds in a minute.
     * 
     * Format: minutes * seconds * milliseconds
     */
    protected static final int expires = 60 * 60 * 60000;

    /** The Cocoon response */
    protected Response response;

    /** The Cocoon request */
    protected Request request;

    private static Logger log = Logger.getLogger(TableExportReader.class);

    DSpaceTableCSV csv = null;
    MetadataExport exporter = null;
    String filename = null;
    /**
     * Set up the export reader.
     * 
     * See the class description for information on configuration options.
     */
    public void setup(SourceResolver resolver, Map objectModel, String src,
            Parameters par) throws ProcessingException, SAXException,
            IOException
    {
        super.setup(resolver, objectModel, src, par);

        try
        {
            this.request = ObjectModelHelper.getRequest(objectModel);
            this.response = ObjectModelHelper.getResponse(objectModel);
            Context context = ContextUtil.obtainContext(objectModel);
            String tableName=par.getParameter("tablename");
            
            if(ControlledVocabularyGestionUtils.isControlledVocabularyImportAllowed(context)){
        	 log.info(LogManager.getHeader(context, "tableexport", "exporting_table:" + tableName));
        	 csv=ControlledVocabularyGestionUtils.exportFile(tableName);
        	 
        	 filename=tableName+"$"+sdf.format(new Date())+".csv";
            }

            else {
                    /*
                     * Auth should be done by MetadataExport -- pass context through
                     * we should just be catching exceptions and displaying errors here
                     *
                     */

                   if(this.request.getSession().getAttribute("dspace.current.user.id")!=null) {
                      String redictURL = request.getContextPath() + "/restricted-resource";
                        HttpServletResponse httpResponse = (HttpServletResponse)
            		objectModel.get(HttpEnvironment.HTTP_RESPONSE_OBJECT);
            		httpResponse.sendRedirect(redictURL);
            		return;
                   }
                   else {
                        String redictURL = request.getContextPath() + "/login";
                        AuthenticationUtil.interruptRequest(objectModel, AUTH_REQUIRED_HEADER, AUTH_REQUIRED_MESSAGE, null);
            		HttpServletResponse httpResponse = (HttpServletResponse)
            		objectModel.get(HttpEnvironment.HTTP_RESPONSE_OBJECT);
            		httpResponse.sendRedirect(redictURL);
            		return;
                   }
            }
        }
        catch (RuntimeException e)
        {
            throw e;    
        }
        catch (Exception e)
        {
            throw new ProcessingException("Unable to read bitstream.",e);
        } 
    }

    
    /**
	 * Write the CSV.
	 * 
	 */
    public void generate() throws IOException, SAXException,
            ProcessingException
    {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition","attachment; filename=" + filename);
 
        out.write(csv.toString().getBytes("UTF-8"));
        out.flush();
        out.close();
    }
    /**
	 * Recycle
	 */
    public void recycle() {        
        this.response = null;
        this.request = null;
    }
}