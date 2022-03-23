/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.cocoon;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.reading.AbstractReader;
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.content.DCDate;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.core.Utils;
import org.xml.sax.SAXException;
/**
 *
 * @author Adán Román Ruiz at arvo.es 
 */
public class CSVStatisticDataReader extends AbstractReader {
    protected static final int BUFFER_SIZE = 8192;
    ItemService itemService = ContentServiceFactory.getInstance().getItemService();
    /**
     * When should a download expire in milliseconds. This should be set to some
     * low value just to prevent someone hitting DSpace repeatily from killing
     * the server. Note: 60000 milliseconds are in a second.
     * 
     * Format: minutes * seconds * milliseconds
     */
    protected static final int expires = 60 * 60 * 60000;

    /** The Cocoon response */
    protected Response response;

    /** The Cocoon request */
    protected Request request;

    private static Logger log = Logger.getLogger(CSVStatisticDataReader.class);

    String result = null;
    String filename = "Estadisticas.csv";

    /**
     * Set up the export reader.
     * 
     * See the class description for information on configuration options.
     */
    public void setup(SourceResolver resolver, Map objectModel, String src,
	    Parameters par) throws ProcessingException, SAXException,
	    IOException {
	super.setup(resolver, objectModel, src, par);

	
	    this.request = ObjectModelHelper.getRequest(objectModel);
	    this.response = ObjectModelHelper.getResponse(objectModel);
	    
        	    String data=request.getParameter("data");         
        	    result = renderCsv(data);

//	    filename = handle.replaceAll("/", "-") + "-" + format + ".txt";

	}
    private String renderCsv(String data) {
	String[] tokens=data.split(",");
	StringBuffer cabecera=new StringBuffer();
	StringBuffer datos=new StringBuffer();
	for(int i=0;i<tokens.length;i++){
	    if(i%2==0){
		if(cabecera.length()!=0){
		    cabecera.append(",");
		}
		cabecera.append(tokens[i]);
	    }else{
		if(datos.length()!=0){
		    datos.append(",");
		}
		datos.append(tokens[i]);
	    }
	}
	return cabecera.toString()+"\n"+datos.toString();
    }
    

    public void generate() throws IOException, SAXException,
	    ProcessingException {

	response.setContentType("text/plain; charset=UTF-8");
	response.setHeader("Content-Disposition", "attachment; filename="
		+ filename);

	out.write(result.getBytes("UTF-8"));
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

   

    public String renderEndNoteFormat(Item[] items, Locale locale) {
	// angenommen ich bekomme eine Liste von Items: items Dann muss diese
	// gerendert werden
	// create a stringbuffer for storing the metadata
	String schema = "dc";
	String ENHead = "%0 ";
	String ENFoot = "%~ GOEDOC, SUB GOETTINGEN";

	// define needed metadatafields
	int bibType = 1;
	// variable document types may need various metadata fields
	String[] DC2Bib = new String[5];
	DC2Bib[1] = "dc.contributor.author, dc.title, dc.relation.ispartofseries, dc.date.issued, dc.identifier.issn, dc.identifier.uri, dc.description.abstract, dc.subject";
	DC2Bib[2] = "dc.contributor, dc.title, dc.publisher, dc.date.issued, dc.identifier.isbn,  dc.identifier.uri, dc.description.abstract, dc.subject";
	DC2Bib[3] = "dc.contributor, dc.title, dc.publisher, dc.date.issued, dc.identifier.isbn,  dc.identifier.uri, dc.description.abstract, dc.subject";

	StringBuffer sb = new StringBuffer();

	// Parsing metadatafields
	for (int i = 0; i < items.length; i++) {
	    boolean tyFound = false;
	    Item item = items[i];
	    // First needed for BibTex: dc.type
	    List<MetadataValue> types = itemService.getMetadata(item,schema, "type", Item.ANY, Item.ANY);

	    for (int j = 0; (j < types.size()) && !tyFound; j++) {
		String type = Utils.addEntities(types.get(j).getValue());

		if (type.equals("Article")) {
		    sb.append(ENHead + "Journal Article");
		    bibType = 1;
		    tyFound = true;
		} else if (type.equals("Book")) {
		    sb.append(ENHead + type);
		    bibType = 2;
		    tyFound = true;
		} else if (type.equals("Book Section")) {
		    sb.append(ENHead + "");
		    bibType = 3;
		    tyFound = true;
		} else if (type.equals("Thesis")) {
		    sb.append(ENHead + "Thesis");
		    bibType = 4;
		    tyFound = true;
		} else if (type.equals("Technical Report")) {
		    sb.append(ENHead + "Report");
		    bibType = 1;
		    tyFound = true;
		} else if (type.equals("Preprint")) {
		    sb.append(ENHead + "Journal Article");
		    bibType = 1;
		    tyFound = true;
		}
	    }

	    // set type in case no type is given
	    if (!tyFound) {
		sb.append(ENHead + "Journal Article");
		bibType = 1;
	    }

	    sb.append(" \n");

	    // Now get all the metadata needed for the requested objecttype
	    StringTokenizer st = new StringTokenizer(DC2Bib[bibType], ",");

	    while (st.hasMoreTokens()) {
		String field = st.nextToken().trim();
		String[] eq = field.split("\\.");
		schema = eq[0];
		String element = eq[1];
		String qualifier = Item.ANY;
		if (eq.length > 2 && eq[2].equals("*")) {
		    qualifier = Item.ANY;
		} else if (eq.length > 2) {
		    qualifier = eq[2];
		}

		List<MetadataValue> values = itemService.getMetadata(item,schema, element, qualifier,Item.ANY);

		// Parse the metadata into a record
		for (int k = 0; k < values.size(); k++) {
		    if (element.equals("contributor")) {
			if (k == 0) {
			    sb.append("%A "
				    + Utils.addEntities(values.get(k).getValue()));
			} else {
			    sb.append("%A "
				    + Utils.addEntities(values.get(k).getValue()));
			}

		    } else if (element.equals("relation")) {
			if (k == 0) {
			    sb.append("%J "
				    + Utils.addEntities(values.get(k).getValue()));
			}
			if (k == 1) {
			    sb.append("%V "
				    + Utils.addEntities(values.get(k).getValue()));
			}
		    } else if (element.equals("title")) {
			if (k == 0) {
			    sb.append("%T "
				    + Utils.addEntities(values.get(k).getValue()));
			}
		    } else if (element.equals("description")) {
			if (k == 0) {
			    sb.append("%X "
				    + Utils.addEntities(values.get(k).getValue()));
			}
		    } else if (element.equals("identifier")
			    && qualifier.equals("issn")) {
			if (k == 0) {
			    sb.append("%@ "
				    + Utils.addEntities(values.get(k).getValue()));
			}
		    }

		    else if (element.equals("identifier")
			    && qualifier.equals("uri")) {
			sb.append("%U " + Utils.addEntities(values.get(k).getValue()));
		    }

		    else if (element.equals("subject")) {
			sb.append("%K " + Utils.addEntities(values.get(k).getValue()));
		    }

		    else if (element.equals("date")) {
			if (k == 0) {
			    // formating the Date
			    DCDate dd = new DCDate(values.get(k).getValue());
			    String date = dd.displayDate(false, false, locale)
				    .trim();
			    int last = date.length();
			    date = date.substring((last - 4), (last));

			    sb.append("%D " + date);
			}
		    } else {
			if (k == 0) {
			    sb.append(qualifier + " "
				    + Utils.addEntities(values.get(k).getValue()));
			}
		    }
		    sb.append("\n");
		}
	    }
	    sb.append(ENFoot + "\n\n");
	}
	String ENData = sb.toString();
	return ENData;
    }
}
