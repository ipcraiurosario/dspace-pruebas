/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.artifactbrowser;

/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.ResourceNotFoundException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.excalibur.source.SourceValidity;
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.AuthenticationUtil;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.Hidden;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.crosswalk.DisseminationCrosswalk;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.handle.factory.HandleServiceFactory;
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
public class FlexpaperViewer extends AbstractDSpaceTransformer implements CacheableProcessingComponent
{

    private static Logger log = Logger.getLogger(FlexpaperViewer.class);

    /**
     * Messages to be sent when the user is not authorized to view
     * a particular bitstream. They will be redirected to the login
     * where this message will be displayed.
     */
    private static final String AUTH_REQUIRED_HEADER = "xmlui.BitstreamReader.auth_header";
    private static final String AUTH_REQUIRED_MESSAGE = "xmlui.BitstreamReader.auth_message";

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

    UUID itemID;
    UUID bitstreamID;
    String handle;

    int sequence;
    String name;

    
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
	//
	try
	{
	    Context context = ContextUtil.obtainContext(objectModel);
	    //	            
	    // Get our parameters that identify the bitstream
	    String parItemID = par.getParameter("itemID", null);
	    itemID = parItemID != null? UUID.fromString(parItemID): new UUID(0, 0);
	    
	    String parBitstreamID = par.getParameter("bitstreamID", null);
	    bitstreamID = parBitstreamID != null? UUID.fromString(parBitstreamID): new UUID(0, 0);
	    
	    handle = par.getParameter("handle", null);

	    sequence = par.getParameterAsInteger("sequence", -1);
	    name = par.getParameter("name", null);
	    //	        
	    this.isSpider = par.getParameter("userAgent", "").equals("spider");
	    //
	    // Resolve the bitstream
	    Bitstream bitstream = null;
	    DSpaceObject dso = null;
	    
	    if (bitstreamID.compareTo(new UUID(0, 0)) != 0)
	    {
		// Direct reference to the individual bitstream ID.
		ContentServiceFactory.getInstance().getBitstreamService().find(context, bitstreamID);
	    }
	    else if (itemID.compareTo(new UUID(0, 0)) != 0)
	    {
		// Referenced by internal itemID
		ContentServiceFactory.getInstance().getItemService().find(context, itemID);

		if (sequence > -1)
		{
		    bitstream = findBitstreamBySequence(item, sequence);
		}
		else if (name != null)
		{
		    bitstream = findBitstreamByName(item, name);
		}
	    }
	    else if (handle != null)
	    {
		// Reference by an item's handle.
		dso = HandleServiceFactory.getInstance().getHandleService().resolveToObject(context,handle);

		if (dso instanceof Item)
		{
		    item = (Item)dso;

		    if (sequence > -1)
		    {
			bitstream = findBitstreamBySequence(item,sequence);
		    }
		    else if (name != null)
		    {
			bitstream = findBitstreamByName(item,name);
		    }
		}
	    }
	    // Was a bitstream found?
	    if (bitstream == null)
	    {
		throw new ResourceNotFoundException("Unable to locate bitstream");
	    }

	    // Is there a User logged in and does the user have access to read it?
	    AuthorizeService authorizeService = AuthorizeServiceFactory.getInstance().getAuthorizeService();
	    boolean isAuthorized = authorizeService.authorizeActionBoolean(context, bitstream, Constants.READ);
	    if (item != null && item.isWithdrawn() && !authorizeService.isAdmin(context))
	    {
		isAuthorized = false;
		//log.info(LogManager.getHeader(context, "view_bitstream", "handle=" + item.getHandle() + ",withdrawn=true"));
	    }

	    if (!isAuthorized)
	    {
		if(context.getCurrentUser() != null){
		    // A user is logged in, but they are not authorized to read this bitstream,
		    // instead of asking them to login again we'll point them to a friendly error
		    // message that tells them the bitstream is restricted.
		    String redictURL = request.getContextPath() + "/handle/";
		    if (item!=null){
			redictURL += item.getHandle();
		    }
		    else if(dso!=null){
			redictURL += dso.getHandle();
		    }
		    redictURL += "/restricted-resource?bitstreamId=" + bitstream.getID();

		    HttpServletResponse httpResponse = (HttpServletResponse)
			    objectModel.get(HttpEnvironment.HTTP_RESPONSE_OBJECT);
		    httpResponse.sendRedirect(redictURL);
		    return;
		}
		else{

		    // The user does not have read access to this bitstream. Interrupt this current request
		    // and then forward them to the login page so that they can be authenticated. Once that is
		    // successful, their request will be resumed.
		    AuthenticationUtil.interruptRequest(objectModel, AUTH_REQUIRED_HEADER, AUTH_REQUIRED_MESSAGE, null);

		    // Redirect
		    String redictURL = request.getContextPath() + "/login";

		    HttpServletResponse httpResponse = (HttpServletResponse)
			    objectModel.get(HttpEnvironment.HTTP_RESPONSE_OBJECT);
		    httpResponse.sendRedirect(redictURL);
		    return;
		}
	    }

	    //	            // Success, bitstream found and the user has access to read it.
	    // Exist the flexpaper version?
	    if(FlexpaperUtils.flexpaperMustBeGenerated(handle,name,sequence,bitstream)){
		FlexpaperUtils.generateFlash(handle,name,sequence,bitstream);
	    }
	}
	catch (SQLException sqle)
	{
	    throw new ProcessingException("Unable to read bitstream.",sqle);
	}
	    }

    /**
     * Find the bitstream identified by a sequence number on this item.
     *
     * @param item A DSpace item
     * @param sequence The sequence of the bitstream
     * @return The bitstream or null if none found.
     */
    private Bitstream findBitstreamBySequence(Item item, int sequence) throws SQLException
    {
	if (item == null)
	{
	    return null;
	}

	List<Bundle> bundles = ContentServiceFactory.getInstance().getItemService().getBundles(item, "ORIGINAL");
	bundles.addAll(ContentServiceFactory.getInstance().getItemService().getBundles(item, "TEXT"));
	bundles.addAll(ContentServiceFactory.getInstance().getItemService().getBundles(item, "THUMBNAIL"));
	for (Bundle bundle : bundles)
	{
	    List<Bitstream> bitstreams = bundle.getBitstreams();

	    for (Bitstream bitstream : bitstreams)
	    {
		if (bitstream.getSequenceID() == sequence)
		{
		    return bitstream;
		}
	    }
	}
	return null;
    }

    /**
     * Return the bitstream from the given item that is identified by the
     * given name. If the name has prepended directories they will be removed
     * one at a time until a bitstream is found. Note that if two bitstreams
     * have the same name then the first bitstream will be returned.
     *
     * @param item A DSpace item
     * @param name The name of the bitstream
     * @return The bitstream or null if none found.
     */
    private Bitstream findBitstreamByName(Item item, String name) throws SQLException
    {
	if (name == null || item == null)
	{
	    return null;
	}

	// Determine our the maximum number of directories that will be removed for a path.
	int maxDepthPathSearch = 3;
	if (ConfigurationManager.getProperty("xmlui.html.max-depth-guess") != null)
	{
	    maxDepthPathSearch = ConfigurationManager.getIntProperty("xmlui.html.max-depth-guess");
	}

	// Search for the named bitstream on this item. Each time through the loop
	// a directory is removed from the name until either our maximum depth is
	// reached or the bitstream is found. Note: an extra pass is added on to the
	// loop for a last ditch effort where all directory paths will be removed.
	for (int i = 0; i < maxDepthPathSearch+1; i++)
	{
	    // Search through all the bitstreams and see
	    // if the name can be found
		List<Bundle> bundles = ContentServiceFactory.getInstance().getItemService().getBundles(item, "ORIGINAL");
		bundles.addAll(ContentServiceFactory.getInstance().getItemService().getBundles(item, "TEXT"));
		bundles.addAll(ContentServiceFactory.getInstance().getItemService().getBundles(item, "THUMBNAIL"));
	    for (Bundle bundle : bundles)
	    {
		List<Bitstream> bitstreams = bundle.getBitstreams();

		for (Bitstream bitstream : bitstreams)
		{
		    if (name.equals(bitstream.getName()))
		    {
			return bitstream;
		    }
		}
	    }

	    // The bitstream was not found, so try removing a directory
	    // off of the name and see if we lost some path information.
	    int indexOfSlash = name.indexOf('/');

	    if (indexOfSlash < 0)
	    {
		// No more directories to remove from the path, so return null for no
		// bitstream found.
		return null;
	    }

	    name = name.substring(indexOfSlash+1);

	    // If this is our next to last time through the loop then
	    // trim everything and only use the trailing filename.
	    if (i == maxDepthPathSearch-1)
	    {
		int indexOfLastSlash = name.lastIndexOf('/');
		if (indexOfLastSlash > -1)
		{
		    name = name.substring(indexOfLastSlash + 1);
		}
	    }

	}

	// The named bitstream was not found and we exhausted the maximum path depth that
	// we search.
	return null;
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
    //No cacheable
    @Override
    public Serializable getKey() {
	return "0";
    }
    // No cacheable
    @Override
    public SourceValidity getValidity() {
	return null;
    }
    /**
     * Add the item's title and trail links to the page's metadata.
     */
    public void addPageMeta(PageMeta pageMeta) throws SAXException,
    WingException, UIException, SQLException, IOException,
    AuthorizeException
    {
	pageMeta.addTrailLink(contextPath + "/",T_dspace_home);
	HandleUtil.buildHandleTrail(context,item,pageMeta,contextPath);
	pageMeta.addTrail().addContent(T_trail);

   }

    /**
     * Display a single item
     */
    public void addBody(Body body) throws SAXException, WingException,
    UIException, SQLException, IOException, AuthorizeException
    {

	String path=FlexpaperUtils.getDocumentUrl(itemID,bitstreamID,handle,name,sequence);
	List<String> flexInfo=FlexpaperUtils.getInfoFlexFile(handle, name, sequence);

	// Build the item viewer division.
	Division division = body.addDivision("flexpaper","flexpaper");
	Hidden flashPath=division.addHidden("flashPath");
	flashPath.setValue(path);
	if(flexInfo!=null && flexInfo.size()>1){
	    Hidden split=division.addHidden("split");
	    split.setValue(flexInfo.get(0));
	    Hidden numpages=division.addHidden("numPages");
	    numpages.setValue(flexInfo.get(1));
	}
    }
    /** Language strings */
    private static final Message T_dspace_home =
	    message("xmlui.general.dspace_home");

    private static final Message T_trail =
	    message("xmlui.ArtifactBrowser.ItemViewer.trail");

    private static final Message T_show_simple =
	    message("xmlui.ArtifactBrowser.ItemViewer.show_simple");

    private static final Message T_show_full =
	    message("xmlui.ArtifactBrowser.ItemViewer.show_full");

    private static final Message T_head_parent_collections =
	    message("xmlui.ArtifactBrowser.ItemViewer.head_parent_collections");

    private static final Message T_withdrawn = message("xmlui.ArtifactBrowser.ItemViewer.withdrawn");
    /**
     * Determine if the full item should be referenced or just a summary.
     */
    public static boolean showFullItem(Map objectModel)
    {
	Request request = ObjectModelHelper.getRequest(objectModel);
	String show = request.getParameter("show");

	if (show != null && show.length() > 0)
	{
	    return true;
	}

	return false;
    }
    /**
     * Obtain the item's title.
     */
    public static String getItemTitle(Item item)
    {
	List<MetadataValue> titles = ContentServiceFactory.getInstance().getItemService().getMetadata(item, "dc", "title", Item.ANY, Item.ANY);

	String title;
	if (titles != null && titles.size() > 0)
	{
	    title = titles.get(0).getValue();
	}
	else
	{
	    title = null;
	}
	return title;
    }
    /** XHTML crosswalk instance */
    private DisseminationCrosswalk xHTMLHeadCrosswalk = null;

    private String sfxFile = ConfigurationManager.getProperty("dspace.dir") + File.separator
	    + "config" + File.separator + "sfx.xml";

    private String sfxQuery = null;
}
