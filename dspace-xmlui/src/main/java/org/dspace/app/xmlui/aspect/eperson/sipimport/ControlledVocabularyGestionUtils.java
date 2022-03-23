package org.dspace.app.xmlui.aspect.eperson.sipimport;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dspace.app.bulkedit.DSpaceCSV;
import org.dspace.app.bulkedit.MetadataImportException;
import org.dspace.app.tablecsv.DSpaceTableCSV;
import org.dspace.app.xmlui.aspect.administrative.FlowResult;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.dspace.eperson.service.GroupService;
import org.dspace.storage.rdbms.DB;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.servlet.multipart.Part;
import org.dspace.app.xmlui.cocoon.servlet.multipart.DSpacePartOnDisk;
import org.apache.cocoon.servlet.multipart.RejectedPart;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.core.LogManager;

public class ControlledVocabularyGestionUtils {
    public static String PREFIX ="vc_";
    
    private static Logger log = Logger.getLogger(ControlledVocabularyGestionUtils.class);
    private static final String cvBackupDir = ConfigurationManager.getProperty("controlled.vocabulary.backup.dir");
    private static final String EXTENSION="UPLOADED";
    private static final Message T_upload_failed = new Message("default", "xmlui.administrative.controlled.vocabulary.import.flow.upload_failed");
    private static final Message T_table_exist = new Message("default", "xmlui.administrative.controlled.vocabulary.import.flow.table_exist");
    private static final Message T_table_not_exist = new Message("default", "xmlui.administrative.controlled.vocabulary.import.flow.table_new");
    
    private static final Message T_import_successful = new Message("default", "xmlui.administrative.cvimport.flow.import_successful");
    private static final Message T_import_failed = new Message("default", "xmlui.administrative.cvimport.flow.import_failed");
    private static final Message T_file_incorrect= new Message("default", "xmlui.administrative.cvimport.flow.file.incorrect");
    
    protected static GroupService groupService = EPersonServiceFactory.getInstance().getGroupService();
    
    private static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSSS");
    
	public static boolean isControlledVocabularyImportAllowed(Context c) {
	    boolean allowed=false;
	    // if we're ignoring authorization, user is member of admin
	    if (c.ignoreAuthorization())
	    {
		return true;
	    }

	    EPerson e = c.getCurrentUser();

	    if (e == null)
	    {
		return false; // anonymous users can't be admins....
	    } else {

		try {
		    List<Group> groups = groupService.allMemberGroups(c, e);
		    String configAllowedGroups=ConfigurationManager.getProperty("controlled.vocabulary.groups");
		    if(StringUtils.isNotEmpty(configAllowedGroups)){
			String[] allowedGroups=configAllowedGroups.split(",");
			for(int i=0;i<allowedGroups.length;i++){
			    for(int j=0;j<groups.size();j++){
				if(allowedGroups[i].equalsIgnoreCase(groups.get(j).getName())){
				    allowed=true;
				}
			    }
			}
		    }
		    //}
		} catch (SQLException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    }
	    return allowed;
	}
	
	public static FlowResult preProcessUploadFile(Context context, Request request) throws SQLException, AuthorizeException, IOException, Exception
	{
	    /**
	     * Solo por precaucion, se crea el directorio cvbackup
	     */
	    if(cvBackupDir!=null){
		File f= new File(cvBackupDir);
		if(!f.exists()){
		    f.mkdirs();
		}
	    }

	    FlowResult result = new FlowResult();
	    result.setContinue(false);

	    Object object = null;

	    if(request.get("file") != null) {
		object = request.get("file");
	    }

	    Part filePart = null;
	    File file = null;

	    if (object instanceof RejectedPart)
	    {
		RejectedPart rejected=(RejectedPart) object;

		result.setContinue(false);
		result.setOutcome(false);
		result.setMessage(T_upload_failed);
		return result;
	    }

	    if (object instanceof Part)
	    {
		filePart = (Part) object;
		file = ((DSpacePartOnDisk)filePart).getFile();
	    }

	    if (filePart != null && filePart.getSize() > 0)
	    {
		String name = filePart.getUploadName();

		while (name.indexOf('/') > -1)
		{
		    name = name.substring(name.indexOf('/') + 1);
		}

		while (name.indexOf('\\') > -1)
		{
		    name = name.substring(name.indexOf('\\') + 1);
		}

		log.info(LogManager.getHeader(context, "cvimport", "loading file"));
		if(name.indexOf("$")!=-1){

		String nombreTabla=name.substring(0, name.indexOf("$"));
		boolean exist=DB.getInstance().existTable(PREFIX+nombreTabla);
		if (exist){
		    request.getSession().setAttribute("mensaje",T_table_exist.parameterize(nombreTabla));
		}else{
		    request.getSession().setAttribute("mensaje",T_table_not_exist.parameterize(nombreTabla));
		}

		File fileConsolidado=new File(file.getCanonicalPath()+EXTENSION);
		file.renameTo(fileConsolidado);
		fileConsolidado.deleteOnExit();

		request.getSession().setAttribute("file", fileConsolidado);
		request.getSession().setAttribute("name", name);
		request.getSession().setAttribute("nombreTabla", PREFIX+nombreTabla);
		result.setContinue(true);
		result.setOutcome(true);
		}else{
		    result.setContinue(false);
		    result.setOutcome(false);
		    result.setMessage(T_file_incorrect);
		}
	    }
	    else
	    {
		result.setContinue(false);
		result.setOutcome(false);
		result.setMessage(T_upload_failed);
	    }

	    return result;
	}
	
	public static FlowResult processUploadFile(Context context, Request request) throws SQLException, AuthorizeException, IOException, Exception
	{
	    FlowResult result = new FlowResult();
	    result.setContinue(false);

	    File file = (File)request.getSession().getAttribute("file");
	    String name = (String)request.getSession().getAttribute("name");
	    if(file.exists()){
		importFile(name,file);
		result.setContinue(true);
		result.setOutcome(true);
		result.setMessage(T_import_successful);
	    }else{
		result.setContinue(false);
		result.setOutcome(false);
		result.setMessage(T_import_failed);
		log.debug(LogManager.getHeader(context, "cvimport", "Changes cancelled"));
	    }


	    return result;
	}
	private static void importFile(String name, File file) throws Exception {
	    String nombreTabla=PREFIX+name.substring(0, name.indexOf("$"));
	    boolean exist=DB.getInstance().existTable(nombreTabla);
	    if (exist){
		DSpaceTableCSV csv;

		csv=new DSpaceTableCSV(file);
		//Copia de seguridad
		String dir=ConfigurationManager.getProperty("controlled.vocabulary.backup.dir");
		File fdir=new File(dir);
		if(!fdir.exists()){
		    fdir.mkdirs(); // Creamos el directorio por si acaso
		}		  
		String nombreBackup=dir+"/"+nombreTabla.substring(PREFIX.length())+"$"+sdf.format(new Date())+"_backup.csv";
		exportFile(nombreTabla.substring(PREFIX.length())).save(nombreBackup);
		//Borrado
		DB.getInstance().emptyTable(nombreTabla);
		//Insertado
		try {
		    csv.insertarBBDD(nombreTabla);
		} catch (SQLException e) {
		    // Si falla se restaura la copia anterior
		    log.error("Error importando valores controlados", e);
		    DB.getInstance().emptyTable(nombreTabla);
		    csv=new DSpaceTableCSV(new File(nombreBackup));
		    csv.insertarBBDD(nombreTabla);
		    throw e;
		}
	    }
	}
	
	public static DSpaceTableCSV exportFile(String tableName) throws Exception {
	    DSpaceTableCSV csv=null;
	    if(DB.getInstance().existTable(PREFIX+tableName)){
		 csv=new DSpaceTableCSV(PREFIX+tableName);
	    }
	    return csv;
	}
}