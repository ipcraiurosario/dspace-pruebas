/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.artifactbrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.ResourceNotFoundException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.reading.AbstractReader;
import org.apache.log4j.Logger;
import org.dspace.content.Bitstream;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.xml.sax.SAXException;

import es.arvo.app.mediafilter.FlexpaperUtils;

/**
 * The BitstreamReader will query DSpace for a particular bitstream and transmit
 * it to the user. There are several methods of specifing the bitstream to be
 * delivered. You may reference a bitstream by either it's id or attempt to
 * resolve the bitstream's name.
 *
 *  /bitstream/{handle}/{sequence}/{name}
 *
 *  &lt;map:read type="BitstreamReader">
 *    &lt;map:parameter name="handle" value="{1}/{2}"/&gt;
 *    &lt;map:parameter name="sequence" value="{3}"/&gt;
 *    &lt;map:parameter name="name" value="{4}"/&gt;
 *  &lt;/map:read&gt;
 *
 *  When no handle is assigned yet you can access a bitstream
 *  using it's internal ID.
 *
 *  /bitstream/id/{bitstreamID}/{sequence}/{name}
 *
 *  &lt;map:read type="BitstreamReader">
 *    &lt;map:parameter name="bitstreamID" value="{1}"/&gt;
 *    &lt;map:parameter name="sequence" value="{2}"/&gt;
 *  &lt;/map:read&gt;
 *
 *  Alternatively, you can access the bitstream via a name instead
 *  of directly through it's sequence.
 *
 *  /html/{handle}/{name}
 *
 *  &lt;map:read type="BitstreamReader"&gt;
 *    &lt;map:parameter name="handle" value="{1}/{2}"/&gt;
 *    &lt;map:parameter name="name" value="{3}"/&gt;
 *  &lt;/map:read&gt;
 *
 *  Again when no handle is available you can also access it
 *  via an internal itemID & name.
 *
 *  /html/id/{itemID}/{name}
 *
 *  &lt;map:read type="BitstreamReader"&gt;
 *    &lt;map:parameter name="itemID" value="{1}"/&gt;
 *    &lt;map:parameter name="name" value="{2}"/&gt;
 *  &lt;/map:read&gt;
 *
 * @author Scott Phillips
 */

public class FlexpaperReader extends AbstractReader implements Recyclable
{
    private static Logger log = Logger.getLogger(FlexpaperReader.class);

    /**
     * Messages to be sent when the user is not authorized to view
     * a particular bitstream. They will be redirected to the login
     * where this message will be displayed.
     */
    private static final String AUTH_REQUIRED_HEADER = "xmlui.BitstreamReader.auth_header";
    private static final String AUTH_REQUIRED_MESSAGE = "xmlui.BitstreamReader.auth_message";
    
    private static final String SWF = "swf";
    private static final String JSON = "json";
    private static final String PNG = "png";

    /**
     * How big a buffer should we use when reading from the bitstream before
     * writing to the HTTP response?
     */
    protected static final int BUFFER_SIZE = 8192;

    /**
     * When should a bitstream expire in milliseconds. This should be set to
     * some low value just to prevent someone hiting DSpace repeatedy from
     * killing the server. Note: there are 1000 milliseconds in a second.
     *
     * Format: minutes * seconds * milliseconds
     *  60 * 60 * 1000 == 1 hour
     */
    protected static final int expires = 60 * 60 * 1000;

    /** The Cocoon response */
    protected Response response;

    /** The Cocoon request */
    protected Request request;

    /** The bitstream file */
    protected InputStream bitstreamInputStream;

    /** The bitstream's reported size */
    protected long bitstreamSize;

    /** The bitstream's mime-type */
    protected String bitstreamMimeType;

    /** The bitstream's name */
    protected String bitstreamName;

    /** True if bitstream is readable by anonymous users */
    protected boolean isAnonymouslyReadable;

    /** Item containing the Bitstream */
    private Item item = null;

    /** True if user agent making this request was identified as spider. */
    private boolean isSpider = false;

    /**
     * Set up the bitstream reader.
     *
     * See the class description for information on configuration options.
     */
    public void setup(SourceResolver resolver, Map objectModel, String src,
	    Parameters par) throws ProcessingException, SAXException,
	    IOException
	    {
	super.setup(resolver, objectModel, src, par);

//	try
//	{
	    this.request = ObjectModelHelper.getRequest(objectModel);
	    this.response = ObjectModelHelper.getResponse(objectModel);

	    // Check to see if a context already exists or not. We may
	    // have been aggregated into an http request by the XSL document
	    // pulling in an XML-based bitstream. In this case the context has
	    // already been created and we should leave it open because the
	    // normal processes will close it.
//	    boolean BitstreamReaderOpenedContext = !ContextUtil.isContextAvailable(objectModel);
//	    Context context = ContextUtil.obtainContext(objectModel);

	    // Get our parameters that identify the bitstream
	    String parItemID = par.getParameter("itemID", null);
	    UUID itemID = parItemID != null? UUID.fromString(parItemID): new UUID(0, 0);
	    
	    String parBitstreamID = par.getParameter("bitstreamID", null);
	    UUID bitstreamID = parBitstreamID != null? UUID.fromString(parBitstreamID): new UUID(0, 0);
	    
	    String handle = par.getParameter("handle", null);

	    int sequence = par.getParameterAsInteger("sequence", -1);
	    int page = par.getParameterAsInteger("page", -1);
	    String name = par.getParameter("name", null);
	    String fileType = par.getParameter("fileType", null);

	    this.isSpider = par.getParameter("userAgent", "").equals("spider");

	    // Resolve the bitstream
	    Bitstream bitstream = null;
	    DSpaceObject dso = null;

	    String path=null;
	    // Por defecto sera el flash
	    if(fileType==null || SWF.equalsIgnoreCase(fileType)){
		path=FlexpaperUtils.getFlashDocumentPath(itemID, bitstreamID, handle, name, sequence,page);
		this.bitstreamInputStream=new FileInputStream(new File(path));
	    }else if(JSON.equalsIgnoreCase(fileType)){
		path=FlexpaperUtils.getJsonDocumentPath(itemID, bitstreamID, handle, name, sequence,page);
		this.bitstreamInputStream=new FileInputStream(new File(path));
	    }else if(PNG.equalsIgnoreCase(fileType)){
	    	path=FlexpaperUtils.getPngDocumentPath(itemID, bitstreamID, handle, name, sequence,page);
	 		this.bitstreamInputStream=new FileInputStream(new File(path));
	 	    }
	    // Was a bitstream found?
	    if (this.bitstreamInputStream == null)
	    {
		throw new ResourceNotFoundException("Unable to locate file");
	    }

	    this.bitstreamName = name;

//	}
//	catch (SQLException sqle)
//	{
//	    throw new ProcessingException("Unable to read bitstream.",sqle);
//	}
    }


    /**
     * Write the actual data out to the response.
     *
     * Some implementation notes:
     *
     * 1) We set a short expiration time just in the hopes of preventing someone
     * from overloading the server by clicking reload a bunch of times. I
     * Realize that this is nowhere near 100% effective but it may help in some
     * cases and shouldn't hurt anything.
     *
     * 2) We accept partial downloads, thus if you lose a connection halfway
     * through most web browser will enable you to resume downloading the
     * bitstream.
     */
    public void generate() throws IOException, SAXException,
    ProcessingException
    {
	if (this.bitstreamInputStream == null)
	{
	    return;
	}

	// Only allow If-Modified-Since protocol if request is from a spider
	// since response headers would encourage a browser to cache results
	// that might change with different authentication.
	if (isSpider)
	{
	    // Check for if-modified-since header -- ONLY if not authenticated
	    long modSince = request.getDateHeader("If-Modified-Since");
	    if (modSince != -1 && item != null && item.getLastModified().getTime() < modSince)
	    {
		// Item has not been modified since requested date,
		// hence bitstream has not been, either; return 304
		response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		return;
	    }
	}

	byte[] buffer = new byte[BUFFER_SIZE];
	int length = -1;

	try
	{
	    //  response.setHeader("Content-Length", String.valueOf(this.bitstreamSize));
	    while ((length = this.bitstreamInputStream.read(buffer)) > -1)
	    {
		out.write(buffer, 0, length);
	    }
	    out.flush();
	}

	finally
	{
	    try
	    {
		// Close the bitstream input stream so that we don't leak a file descriptor
		this.bitstreamInputStream.close();

		// Close the output stream as per Cocoon docs: http://cocoon.apache.org/2.2/core-modules/core/2.2/681_1_1.html
		out.close();
	    } 
	    catch (IOException ioe)
	    {
		// Closing the stream threw an IOException but do we want this to propagate up to Cocoon?
		// No point since the user has already got the bitstream contents.
		log.warn("Caught IO exception when closing a stream: " + ioe.getMessage());
	    }
	}
    }

    /**
     * Returns the mime-type of the bitstream.
     */
    public String getMimeType()
    {
	return this.bitstreamMimeType;
    }

    /**
     * Recycle
     */
    public void recycle() {
	this.response = null;
	this.request = null;
	this.bitstreamInputStream = null;
	this.bitstreamSize = 0;
	this.bitstreamMimeType = null;
    }
}