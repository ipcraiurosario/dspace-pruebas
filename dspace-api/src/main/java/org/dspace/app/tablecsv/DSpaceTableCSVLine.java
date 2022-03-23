/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.tablecsv;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dspace.storage.rdbms.DB;

/**
 * Utility class to store a line from a CSV file
 *
 * @author Stuart Lewis
 */
public class DSpaceTableCSVLine implements Serializable
{
    /** The item id of the item represented by this line. -1 is for a new item */
    private int id;

    /** The elements in this line in a hashtable, keyed by the metadata type */
    private Map<String, String> items;

    /**
     * Create a new CSV line
     *
     * @param itemId The item ID of the line
     */
    public DSpaceTableCSVLine(int itemId)
    {
        // Store the ID + separator, and initialise the hashtable
        this.id = itemId;
        items = new HashMap<String, String>();
    }

    /**
     * Create a new CSV line for a new item
     */
    public DSpaceTableCSVLine()
    {
        // Set the ID to be -1, and initialise the hashtable
        this.id = -1;
        this.items = new HashMap<String, String>();
    }

    /**
     * Get the item ID that this line represents
     *
     * @return The item ID
     */
    public int getID()
    {
        // Return the ID
        return id;
    }

    /**
     * Add a new metadata value to this line
     *
     * @param key The metadata key (e.g. dc.contributor.author)
     * @param value The metadata value
     */
    public void add(String key, String value)
    {
       
            items.put(key,value);
           }

    /**
     * Get all the values that match the given metadata key. Will be null if none exist.
     *
     * @param key The metadata key
     * @return All the elements that match
     */
    public String get(String key)
    {
        // Return any relevant values
        return items.get(key);
    }

     /**
     * Get all the metadata keys that are represented in this line
     *
     * @return An enumeration of all the keys
     */
    public Set<String> keys()
    {
        // Return the keys
        return items.keySet();
    }

    /**
     * Write this line out as a CSV formatted string, in the order given by the headings provided
     *
     * @param headings The headings which define the order the elements must be presented in
     * @return The CSV formatted String
     */
    protected String toCSV(List<String> headings)
    {
        StringBuilder bits = new StringBuilder();

        // Add the rest of the elements
        for (String heading : headings)
        {
            // Para el primero no hay coma
            if(!headings.get(0).equals(heading)){
        	bits.append(DSpaceTableCSV.fieldSeparator);
            }
            String value = items.get(heading);
            if (value != null)
            {
                bits.append(valueToCSV(value));
            }
        }

        return bits.toString();
    }

    /**
     * Internal method to create a CSV formatted String joining a given set of elements
     *
     * @param values The values to create the string from
     * @return The line as a CSV formatted String
     */
    protected String valueToCSV(String values)
    {
        // Check there is some content
        if (values == null)
        {
            return "";
        }

        // Replace internal quotes with two sets of quotes
        return "\"" + values.replaceAll("\"", "\"\"").replaceAll("'", "''") + "\"";
    }

    public String toInsert(String nombreTabla) throws SQLException {
	Set keyset=items.keySet();
	ArrayList keys=new ArrayList();
	ArrayList values=new ArrayList();
	HashMap<String,String> datatypes=DB.getInstance().getDatatypes(nombreTabla);
	
	Iterator<String> it=keyset.iterator();
	while(it.hasNext()){
	    String key=it.next();
	    if(items.get(key)!=null && !items.get(key).equals(" ")){
		    keys.add(key);
		    values.add(items.get(key));
	    }
	}
	
	StringBuffer sb=new StringBuffer();
	sb.append("insert into ");
	sb.append(nombreTabla);
	sb.append("(");
	for(int i=0;i<keys.size();i++){
	    if(i!=0){
		sb.append(",");
	    }
	    sb.append(keys.get(i));
	}
	sb.append(") values (");
	for(int i=0;i<values.size();i++){
	    boolean textType=false;
	    // No existe la columna en la tabla. Fallara
	    if(datatypes.get(keys.get(i))==null){
		throw new SQLException("No existe la columna:"+keys.get(i));
	    }
	    if((((String)datatypes.get(keys.get(i))).equalsIgnoreCase("character varying")) || 
		 (((String)datatypes.get(keys.get(i))).equalsIgnoreCase("text")) || 
		 (((String)datatypes.get(keys.get(i))).equalsIgnoreCase("character")) ||
		 /* oracle*/
		 (((String)datatypes.get(keys.get(i))).equalsIgnoreCase("VARCHAR2"))){
		textType=true;
	    }
	    if(i!=0){
		sb.append(",");
	    }
	    if(textType){
		sb.append("'");
		sb.append(values.get(i));
		sb.append("'");
	    }else{
		sb.append(values.get(i));
	    }
	}
	sb.append(")");
	//sb.append(";");
	return sb.toString();
    }
    
}
