/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authority;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.dspace.core.ConfigurationManager;

/**
 *
 * @author Antoine Snyers (antoine at atmire.com)
 * @author Kevin Van de Velde (kevin at atmire dot com)
 * @author Ben Bosman (ben at atmire dot com)
 * @author Mark Diggory (markd at atmire dot com)
 */
public class ArvoPersonAuthorityValue extends PersonAuthorityValue {

    private Map<String, String> extraInfo=new HashMap<String,String>();
    
    public Map<String, String> getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(Map<String, String> extraInfo) {
        this.extraInfo = extraInfo;
    }

    public ArvoPersonAuthorityValue() {
    }

    public ArvoPersonAuthorityValue(SolrDocument document) {
        super(document);
    }

    @Override
    public Map<String, String> choiceSelectMap() {

        Map<String, String> map = super.choiceSelectMap();

        String authorExtraParams=ConfigurationManager.getProperty("sql.author.extraparams");
        if(StringUtils.isNotBlank(authorExtraParams)){
            String[] params= authorExtraParams.split(",");
            if(params!=null && params.length>0){
        	for(int i=0;i<params.length;i++){
        	    if(extraInfo.get(params[i])!=null){
        		map.put(params[i], extraInfo.get(params[i]));
        	    }
        	}
            }
        }
        
//        if (StringUtils.isNotBlank(getFirstName())) {
//            map.put("first-name", getFirstName());
//        } else {
//            map.put("first-name", "/");
//        }
//
//        if (StringUtils.isNotBlank(getLastName())) {
//            map.put("last-name", getLastName());
//        } else {
//            map.put("last-name", "/");
//        }
//
//        if (!getEmails().isEmpty()) {
//            boolean added = false;
//            for (String email : getEmails()) {
//                if (!added && StringUtils.isNotBlank(email)) {
//                    map.put("email",email);
//                    added = true;
//                }
//            }
//        }
//        if (StringUtils.isNotBlank(getInstitution())) {
//            map.put("institution", getInstitution());
//        }

        return map;
    }


    @Override
    public boolean equals(Object obj) {
	if ( this == obj ) return true;
	if ( !(obj instanceof AuthorityValue) ) return false;

	AuthorityValue that = (AuthorityValue)obj;
	if(this.getId().equals(that.getId()) /*&&
	   this.getAuthorityType().equals(this.getAuthorityType()) &&
	   this.getId().equals(this.getId()) &&
	   this.getLastName().equals(this.getLastName()) &&
	   this.getFirstName().equals(this.getFirstName())*/){
	    return true;
	}
	return false;
    }   
 }