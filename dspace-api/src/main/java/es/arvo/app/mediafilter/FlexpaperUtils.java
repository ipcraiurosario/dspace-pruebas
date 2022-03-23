package es.arvo.app.mediafilter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.xml.dtm.ref.DTMNodeIterator;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.handle.factory.HandleServiceFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

public class FlexpaperUtils
{
  public static String TIPO_FLASH = "application/x-shockwave-flash";
    
  public static String TIPO_PDF = "application/pdf";
  public static String TIPO_TIFF = "image/tiff";
  public static String TIPO_GIF = "image/gif";
  public static String TIPO_BMP = "image/x-ms-bmp";
  public static String TIPO_RAW = "image/raw";
  public static String TIPO_WAV = "audio/x-wav";
  public static String TIPO_PNG = "image/png";
  public static String TIPO_JPEG = "image/jpeg";
  // Tipos de openoffice
  
  public static String TIPO_EXCEL = "application/vnd.ms-excel"; //XLS
  public static String TIPO_MSWORD = "application/msword"; //DOC
  public static String TIPO_HTML = "text/html";
  public static String TIPO_OPENDOCUMENT_TEXT = "application/vnd.oasis.opendocument.text"; //ODT
  public static String TIPO_OPENOFFICE_TEXT ="application/vnd.sun.xml.writer"; //SXW
  public static String TIPO_MSWORD_2007 ="application/vnd.openxmlformats-officedocument.wordprocessingml.document"; //DOCX
  public static String TIPO_RICH_TEXT ="text/rtf"; //RTF
  public static String TIPO_WORD_PERFECT = "application/wordperfect"; //WPD
  public static String TIPO_TEXT = "text/plain"; //TXT
  public static String TIPO_OPENDOCUMENT_SPREADSHEET = "application/vnd.oasis.opendocument.spreadsheet"; //ODS
  public static String TIPO_OPENOFFICE_SPREADSHEET = "application/vnd.sun.xml.calc"; //SXC
  public static String TIPO_MSEXCELL_2007 = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"; //XLSX
  public static String TIPO_CSV = "text/csv"; //CSV
  public static String TIPO_TSV = "text/tab-separated-values"; //TSV
  public static String TIPO_OPENDOCUMENT_PRESENTATION="application/vnd.oasis.opendocument.presentation"; //ODP
  public static String TIPO_OPENOFFICE_PRESENTATION ="application/vnd.sun.xml.impress"; //SXI
  public static String TIPO_MSPRESENTATION = "application/vnd.ms-powerpoint"; //PPT
  public static String TIPO_MSPRESENTATION_2007="application/vnd.openxmlformats-officedocument.presentationml.presentation"; //PPTX
  public static String TIPO_OPENDOCUMENT_DRAWING = "application/vnd.oasis.opendocument.graphics"; //ODG
  public static String TIPO_SCALABLE_VECTOR_GRAPHICS ="image/svg+xml"; //SVG
  
  
  public static HashMap<String,String> tipoOpenOffice=new HashMap(){{
      						put(TIPO_EXCEL,"");
  						put(TIPO_MSWORD,"");
  						put(TIPO_HTML,"");
  						put(TIPO_OPENDOCUMENT_TEXT,"");
  						put(TIPO_OPENOFFICE_TEXT,"");
  						put(TIPO_MSWORD_2007,"");
  						put(TIPO_RICH_TEXT,"");
  						put(TIPO_WORD_PERFECT,"");
  						put(TIPO_TEXT,"");
  						put(TIPO_OPENDOCUMENT_SPREADSHEET,"");
  						put(TIPO_OPENOFFICE_SPREADSHEET,"");
  						put(TIPO_MSEXCELL_2007,"");
  						put(TIPO_CSV,"");
  						put(TIPO_TSV,"");
  						put(TIPO_OPENDOCUMENT_PRESENTATION,"");
  						put(TIPO_OPENOFFICE_PRESENTATION,"");
  						put(TIPO_MSPRESENTATION,"");
  						put(TIPO_MSPRESENTATION_2007,"");
  						put(TIPO_OPENDOCUMENT_DRAWING,"");
  						put(TIPO_SCALABLE_VECTOR_GRAPHICS,"");
  
  }};

  public static String FLEX_TROCEADO = "split";
  public static String FLEX_NO_TROCEADO = "full";

  static String flashBaseUrl = ConfigurationManager.getProperty("flexpaper.flash.dir");
  static boolean flexpaperEnabled = ConfigurationManager.getBooleanProperty("flexpaper.enabled");

  static boolean pdfSplit = ConfigurationManager.getBooleanProperty("pdf2swf.split");
  static String flexpaperFormats = ConfigurationManager.getProperty("flexpaper.formats");

  static String pdf2swfExe = ConfigurationManager.getProperty("pdf2swf.exe");
  static String png2swfExe = ConfigurationManager.getProperty("png2swf.exe");
  static String jpeg2swfExe = ConfigurationManager.getProperty("jpeg2swf.exe");
  static String pdf2jsonExe = ConfigurationManager.getProperty("pdf2json.exe");
  static String mudrawExe = ConfigurationManager.getProperty("mudraw.exe");

  static String iipBaseUrl = ConfigurationManager.getProperty("iipimage.tiffdir");
  static boolean iipViewerEnabled = ConfigurationManager.getBooleanProperty("iipimage.enabled");
  static String iipFormats = ConfigurationManager.getProperty("iipimage.formats");
  
  static String openofficeHost = ConfigurationManager.getProperty("oo.host");
  static int openofficePort = ConfigurationManager.getIntProperty("oo.port");
  static boolean openofficeEnabled = ConfigurationManager.getBooleanProperty("oo.enabled",false);
  
  
  private static Logger log = Logger.getLogger(FlexpaperUtils.class);
  private static String previewFormats;
  
  static{
	previewFormats=ConfigurationManager.getProperty("preview.formats");
  }
  
  private static Context context = new Context();
  
  public synchronized static boolean generateFlash(String handle, String name, int sequence, Bitstream bitstream) throws SQLException
  {
    File base = new File(flashBaseUrl+"/"+handle);

    if (!base.exists()) {
      log.warn("No existe el directorio de flash de FLEXPAPER. Se crea uno vacio");
      if (!base.mkdirs()) {
        log.warn("No se ha podico crear el directorio de flash de FLEXPAPER: " + flashBaseUrl);
      }
    }
    String tipoArchivo = getTipoArchivo(bitstream);
    File tempOrigen=null;
    File tempDestino=null;
    try
    {
		String sufix = ".tmp";
		if (tipoArchivo.equalsIgnoreCase(TIPO_PNG)) {
			sufix = ".png";
		} else if (tipoArchivo.equalsIgnoreCase(TIPO_JPEG)) {
			sufix = ".jpg";
		} else if (tipoArchivo.equalsIgnoreCase(TIPO_PDF)) {
			sufix = ".pdf";
		}
	  tempOrigen = File.createTempFile("orig", sufix, base);
      tempOrigen.deleteOnExit();
      tempDestino = File.createTempFile("dest", sufix, base);
      tempDestino.deleteOnExit();
      
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempOrigen));
      InputStream bitstreamInputStream = ContentServiceFactory.getInstance().getBitstreamService().retrieve(context, bitstream);
      IOUtils.copy(bitstreamInputStream, bos);
      bos.close();
      // Lo primero es usar el openoffice para pasarlo a pdf si se puede
      if(openofficeEnabled && tipoOpenOffice.containsKey(tipoArchivo)){
		  convertirToPdf(tempOrigen,tempDestino,tipoArchivo);
		  //tempOrigen=tempDestino;
		  BufferedInputStream bis = new BufferedInputStream(new FileInputStream(tempDestino));
		  bos = new BufferedOutputStream(new FileOutputStream(tempOrigen));
		  IOUtils.copy(bis, bos);
		  bis.close();
		  bos.close();
		  tipoArchivo=TIPO_PDF;
      }
      Process p=null;
      Process pjson=null;
      Process phtml4=null;
      if (tipoArchivo.equalsIgnoreCase(TIPO_PNG)) {
        String nombreFicheroDestino = generateSwfFileName(handle, name, sequence,tipoArchivo);
        p = Runtime.getRuntime().exec(png2swfExe + " " + tempOrigen + " -o " + base + "/" + nombreFicheroDestino + " -T 9");
      }else if (tipoArchivo.equalsIgnoreCase(TIPO_JPEG)) {
        String nombreFicheroDestino = generateSwfFileName(handle, name, sequence,tipoArchivo);
        p = Runtime.getRuntime().exec(jpeg2swfExe + " " + tempOrigen + " -o " + base + "/" + nombreFicheroDestino + " -q 100 -T 9");
      }else if (tipoArchivo.equalsIgnoreCase(TIPO_PDF)) {
        String nombreFicheroDestino = generateSwfFileName(handle, name, sequence,tipoArchivo);
        if(ConfigurationManager.getProperty("dspace.dir").contains(":")){
            p = Runtime.getRuntime().exec(pdf2swfExe + " " + tempOrigen + " -o " + base + "/" + nombreFicheroDestino + " -f -T 9 -t -j 71 -s poly2bitmap -s storeallcharacters --height 842 --width 595");
        }else{
            p = Runtime.getRuntime().exec(pdf2swfExe + " " + tempOrigen + " -o " + base + "/" + nombreFicheroDestino + " -f -T 9 -t -j 71 -s poly2bitmap -s storeallcharacters -Q 900 --height 842 --width 595");
        }
      }
      
      String nombreJson = generateJsonFileName(handle, name, sequence);
      
      if(tipoArchivo.equalsIgnoreCase(TIPO_JPEG) || tipoArchivo.equalsIgnoreCase(TIPO_PNG)){
    	  String rutaJSON = base + "/" + nombreJson.replace("%", "-1");
    	  File file = new File(rutaJSON);
    	  file.getParentFile().mkdirs();
    	  file.createNewFile();
    	  BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    	  bw.write("[{\"text\":[]}]");
    	  bw.close();
      }
	  pjson = Runtime.getRuntime().exec(pdf2jsonExe + " " + tempOrigen + " -enc UTF-8 -compress -split 10 " + base + "/" + nombreJson);
      String nombreHtml4 = generateHtml4FileName(handle, name, sequence);
      phtml4= Runtime.getRuntime().exec(mudrawExe + " -r100 -o " + base + "/" + nombreHtml4 + " "+tempOrigen);
      
      log.debug("Proceso generador de swf terminado."+ pdf2swfExe==null?"ES NULL":"Resultado no null");
      
      StringBuffer logger = new StringBuffer();
      StreamEater errEater = StreamEater.eat(p.getErrorStream(), logger);
      StreamEater outEater = StreamEater.eat(p.getInputStream(), logger);
      try {
	  p.waitFor();
	  phtml4.waitFor();
      } catch (InterruptedException e) {
	  log.error(e);
      }if(p.exitValue()==0){
          StreamEater jsonerrEater = StreamEater.eat(pjson.getErrorStream(), logger);
          StreamEater jsonoutEater = StreamEater.eat(pjson.getInputStream(), logger);
          try {
    	  pjson.waitFor();
          } catch (InterruptedException e) {
    	  log.error(e);
          }
	log.info(logger.toString());
          crearFicheroInfo(handle,generateInfoFlexFileName(handle, name, sequence), tempOrigen, tipoArchivo);
      }if(phtml4.exitValue()==0){
          StreamEater phtml4inEater = StreamEater.eat(phtml4.getErrorStream(), logger);
          StreamEater phtml4outEater = StreamEater.eat(phtml4.getInputStream(), logger);
          try {
        	  phtml4.waitFor();
          } catch (InterruptedException e) {
    	  log.error(e);
          }
          log.info(logger.toString());
          crearFicheroInfo(handle,generateInfoFlexFileName(handle, name, sequence), tempOrigen, tipoArchivo);
      }
//      if(p!=null){
//	  List<String> resultado=IOUtils.readLines(p.getInputStream());
//	  List<String> errores=IOUtils.readLines(p.getErrorStream());
//	  
//	  for(int i=0;i<resultado.size();i++){
//	      log.debug(resultado.get(i));
//	  }
//	  for(int i=0;i<errores.size();i++){
//	      log.warn(errores.get(i));
//	  }
//	  if(pjson!=null){
//	      List<String> resultadoJson=IOUtils.readLines(pjson.getInputStream());
//	      List<String> erroresJson=IOUtils.readLines(pjson.getErrorStream());
//	      for(int i=0;i<resultadoJson.size();i++){
//		  log.debug(resultadoJson.get(i));
//	      }
//	      for(int i=0;i<erroresJson.size();i++){
//		  log.warn(erroresJson.get(i));
//	      }
//	  }
//	 
//	  //Creamos fichero "flex"
//	  try {
//	      if(p.waitFor()==0){
//		  crearFicheroInfo(generateInfoFlexFileName(handle, name, sequence), tempOrigen, tipoArchivo);
//	      }
//	  } catch (IllegalThreadStateException e) {
//	      log.warn("Estado ilegal del proceso de creacion de swf",e);
//	      p.destroy();
//	  }catch (InterruptedException e) {
//	      log.warn("Proceso de creacion del swf interrumpido",e);
//	      p.destroy();
//	      e.printStackTrace();
//	}
//	  // DUDOSO: Si el proceso es null sin excepciones suponemos que fue bien
// 
//      }else{
//	  log.debug("Proceso es null pero sin errores.Creamos fichero flex");
//	  crearFicheroInfo(generateInfoFlexFileName(handle, name, sequence), tempOrigen, tipoArchivo);
//      }
    } catch (IOException e) {
      log.error(e);
      e.printStackTrace();
    } catch (SQLException e) {
      log.error(e);
      e.printStackTrace();
    } catch (AuthorizeException e) {
      log.error(e);
      e.printStackTrace();
    }finally{
	if(tempDestino!=null && tempDestino.exists()){
	    try {
		tempDestino.delete();
	    } catch (Exception e) {/*nada*/}
	}
	if(tempOrigen!=null && tempOrigen.exists()){
	    try{
		tempOrigen.delete();
	    } catch (Exception e) {/*nada*/}
	}
    }
    return true;
  }

private static void convertirToPdf(File tempOrigen, File tempDestino,
	String tipoArchivo) throws ConnectException {
      OpenOfficeConnection connection = new SocketOpenOfficeConnection(openofficeHost,openofficePort);
      try {
	connection.connect();
    
       
      // convert
      DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
      DocumentFormatRegistry formats=new DefaultDocumentFormatRegistry();
      
      DocumentFormat inputFormat=formats.getFormatByMimeType(tipoArchivo);
      DocumentFormat pdfFormat=formats.getFormatByMimeType(TIPO_PDF);
            
      converter.convert(tempOrigen,inputFormat, tempDestino,pdfFormat);
      } catch (ConnectException e) {
	log.warn("No se puede conectar al servicion openoffice", e);
	throw(e);
      }finally{
	      // close the connection
	      try {
		connection.disconnect();
	    } catch (Exception e) { /*No hacemos nada*/}
      }
    
}

private static void crearFicheroInfo(String handle,String flexFilename, File pdfFile, String tipoArchivo) {
    File f = new File(flashBaseUrl + "/" + flexFilename);
    BufferedWriter bos = null;
    PDDocument pdf = null;
    try {
      bos = new BufferedWriter(new FileWriter(f));
      if ((pdfSplit) && (TIPO_PDF.equalsIgnoreCase(tipoArchivo)))
        bos.write(FLEX_TROCEADO);
      else {
        bos.write(FLEX_NO_TROCEADO);
      }
      bos.write("\n");
      if (TIPO_PDF.equalsIgnoreCase(tipoArchivo)) {
 
    	  bos.write(getNumPages(handle,pdfFile,flexFilename));
      } else {
        bos.write("1");
      }
    } catch (FileNotFoundException e) {
      log.error(e);
      e.printStackTrace();

      if (pdf != null)
        try {
          pdf.close();
        } catch (IOException localIOException1) {
        }
      if (bos != null)
        try {
          bos.close();
        }
        catch (IOException localIOException2)
        {
        }
    }
    catch (IOException e)
    {
      log.error(e);
      e.printStackTrace();

      if (pdf != null)
        try {
          pdf.close();
        } catch (IOException localIOException3) {
        }
      if (bos != null)
        try {
          bos.close();
        }
        catch (IOException localIOException4)
        {
        }
    }
    finally
    {
      if (pdf != null)
        try {
          pdf.close();
        } catch (IOException localIOException5) {
        }
      if (bos != null)
        try {
          bos.close();
        }
        catch (IOException localIOException6)
        {
        }
    }
  }

private static String getNumPages(String handle,File pdfFile, String flexFilename) throws IOException {
	// Falla a veces
	PDDocument pdf = null;
	pdf = PDDocument.load(pdfFile);
	if(pdf.getNumberOfPages()!=0){
		return ""+pdf.getNumberOfPages();
	}else{
		String pattern=flexFilename.substring(0, flexFilename.indexOf("."));
		String dir=flashBaseUrl+"/"+handle;
		SwfFileFilter swfFilter = new FlexpaperUtils().new SwfFileFilter(pattern);
		String[] myFiles = new File(dir).list(swfFilter);
		if(myFiles.length>0){
			return ""+myFiles.length;
		}else{
			return "0";
		}
	}
}

//TODO: Por si en un futuro hay que generar tiffs piramidales a partir de algo
  public static boolean generateTiff(String handle, String name, int sequence, Bitstream bitstream) {
    return true;
  }

  private static String getTipoArchivo(Bitstream bitstream) throws SQLException
  {
	return ContentServiceFactory.getInstance().getBitstreamService().getFormat(context, bitstream).getMIMEType();
  }

  public static boolean flexpaperLinkMustBeShown(String mimetype,String bitstream) throws  UnsupportedEncodingException {
    if (StringUtils.isNotBlank(mimetype) && (flexpaperEnabled) && (flexpaperFormats != null) && (flexpaperFormats.indexOf(mimetype) != -1) && isAllowed(bitstream)) {
      return true;
    }
    return false;
  }
  
  private static boolean isAllowed(String bitstream) {
    if(bitstream.contains("isAllowed=y")){
	return true;
    }else{
	return false;
    }
}

public static boolean iipLinkMustBeShown(String mimetype,String bitstream) {
    if ((iipViewerEnabled) && 
      (iipFormats != null) && (iipFormats.indexOf(mimetype) != -1)) {
      return true;
    }
    return false;
  }

  public static boolean flexpaperMustBeGenerated(String handle, String name, int sequence, Bitstream bitstream) throws SQLException
  {
    if ((flexpaperEnabled) && 
      (flexpaperFormats != null) && (flexpaperFormats.indexOf(getTipoArchivo(bitstream)) != -1)) {
      String nombreFichero = flashBaseUrl + "/" + generateInfoFlexFileName(handle, name, sequence);
      File fichero = new File(nombreFichero);
      if (fichero.exists()) {
        List info = getInfoFlexFile(handle, name, sequence);
        if (((info != null ? 1 : 0) & (info.size() > 0 ? 1 : 0)) != 0) {
          return false;
        }
        else {
          return true;
        }
      }else{
	  return true;
      }
    }

    return false;
  }
  public static int getFirstPreviewIndex(Object mimetype){
      int currentNode=0;
      Node node=((DTMNodeIterator)mimetype).nextNode();
      
      while (node!=null){
	  NodeList files=node.getChildNodes();
	  for(int i=0;i<files.getLength();i++){
		  Node file=files.item(i);
		  if(file!=null && file.getNodeName().equalsIgnoreCase("mets:file")){
			  currentNode++;
			  if(previewFormats.contains(file.getAttributes().getNamedItem("MIMETYPE").getNodeValue())){
				 return currentNode; 
			  }
		  }
	  }  
	  node=((DTMNodeIterator)mimetype).nextNode();
      }
      return -1;
  }
  public static List<String> getInfoFlexFile(String handle, String name, int sequence) {
	  log.debug("Obteniendo FlexFile. Handle:"+handle+";name:"+name+";sequence:"+sequence);
    String nombreFichero = flashBaseUrl + "/" + generateInfoFlexFileName(handle, name, sequence);
    log.debug("Obteniendo FlexFile. nombreFichero:"+nombreFichero);
    File fichero = new File(nombreFichero);
    if (fichero.exists()) {
      try {
        return FileUtils.readLines(fichero);
      } catch (IOException e) {
        log.warn("Error 15:Error abriendo fichero",e);
        e.printStackTrace();
      }
    }else{
	    log.info("Error 14:No existe fichero:"+nombreFichero);
    }
    return null;
  }
  
  // TODO: Indica si hay que generar un tiff piramidal a partir del que hay en dspace.
  public static boolean tiffMustBeGenerated(String handle, String name, int sequence, Bitstream bitstream) throws SQLException
  {
    if ((iipViewerEnabled) && 
      (iipFormats != null)) iipFormats.indexOf(getTipoArchivo(bitstream));

    return false;
  }

  private static String generateTiffFileName(String handle, String name, int sequence) {
      String cleanName= cleanName(name);
	  String nombre=(cleanName + ".tiff").replaceAll("[//]", "_").replaceAll("\\s", "_");
	  log.debug("Nombre flexfile generado:"+nombre);
    return nombre;
  }

  private static String generateInfoFlexFileName(String handle, String name, int sequence) {
      String cleanName= cleanName(name);
    return (handle + "_" + cleanName + "_" + sequence + ".flex").replaceAll("[//]", "_").replaceAll("\\s", "_");
  }

  private static String generateSwfFileName(String handle, String name, int sequence,String tipoArchivo) {
      String cleanName= cleanName(name);
    if (pdfSplit  && TIPO_PDF.equalsIgnoreCase(tipoArchivo)) {
      return (handle + "_" + cleanName + "_" + sequence + "%.swf").replaceAll("[//]", "_").replaceAll("\\s", "_");
    }
    return (handle + "_" + cleanName + "_" + sequence + ".swf").replaceAll("[//]", "_").replaceAll("\\s", "_");
  }

  private static String generateSwfDownloadFileName(String handle, String name, int sequence, int page) {
      String cleanName= cleanName(name);
    if (page != -1) {
      return (handle + "_" + cleanName + "_" + sequence + page + ".swf").replaceAll("[//]", "_").replaceAll("\\s", "_");
    }
    return (handle + "_" + cleanName + "_" + sequence + ".swf").replaceAll("[//]", "_").replaceAll("\\s", "_");
  }
  
	private static String generateJsonDownloadFileName(String handle, String name, int sequence, int page) {
		String cleanName = cleanName(name);
		// if (page != -1) {
		return (handle + "_" + cleanName + "_" + sequence + page + ".json").replaceAll("[//]", "_").replaceAll("\\s",
				"_");
		// }
		// return (handle + "_" + cleanName + "_" + sequence +
		// ".json").replaceAll("[//]", "_").replaceAll("\\s", "_");
	}
  
  public static String getJsonDocumentPath(UUID itemID, UUID bitstreamID, String handle, String name, int sequence, int page)
  {
    return flashBaseUrl + "/" + handle+"/"+ generateJsonDownloadFileName(handle, name, sequence, page);
  }
  
	private static String generateHtml4FileName(String handle, String name, int sequence) {
		String cleanName = cleanName(name);
		return (handle + "_" + cleanName + "_" + sequence + "%d.png").replaceAll("[//]", "_").replaceAll("\\s", "_");
	}

	private static String generateJsonFileName(String handle, String name, int sequence) {
		String cleanName = cleanName(name);

		return (handle + "_" + cleanName + "_" + sequence + "%.json").replaceAll("[//]", "_").replaceAll("\\s", "_");
	}
  
  public static String getFlashDocumentPath(UUID itemID, UUID bitstreamID, String handle, String name, int sequence, int page)
  {
    return flashBaseUrl + "/" + handle+"/"+generateSwfDownloadFileName(handle, name, sequence, page);
  }
  
	public static String getPngDocumentPath(UUID itemID, UUID bitstreamID, String handle, String name, int sequence,
			int page) {
		return flashBaseUrl + "/" + handle + "/" + generatePngDownloadFileName(handle, name, sequence, page);
	}

	private static String generatePngDownloadFileName(String handle, String name, int sequence, int page) {
		String cleanName = cleanName(name);
		if (page != -1) { // Aqui esta el problema...
			return (handle + "_" + cleanName + "_" + sequence + page + ".png").replaceAll("[//]", "_").replaceAll("\\s",
					"_");
		}
		return (handle + "_" + cleanName + "_" + sequence + ".png").replaceAll("[//]", "_").replaceAll("\\s", "_");
	}

  public static String getTiffDocumentPath(UUID itemID, UUID bitstreamID, String handle, String name, int sequence)
  {
    return iipBaseUrl + "/" + generateTiffFileName(handle, name, sequence);
  }

  public static String getDocumentUrl(UUID itemID, UUID bitstreamID, String handle, String name, int sequence)
  {
    return "/flexpaper/file/" + handle + "/" + name + "?sequence=" + sequence;
  }
  
  // AÃ±adidos para la previsualizacion en pagina principal
  
  /**
   * Devuelve el "n" item de un string separado por comas
   * @param cadena
   * @param pos
   * @return
   */
  public static String tokenizePos(String cadena,int pos,String regex){
      if(cadena!=null){
	  return cadena.split(regex)[pos];
      }
      return "";
  }
  /**
   * obtiene los datos para que flexpaper dibuje el documento desde el item-view. viene a ser el preview. genera el flash si es necesario. Ojo, se salta los permisos!!
   * @param bitstreamParam
   * @return "url,split,numPages"
   * @throws ResourceNotFoundException
 * @throws UnsupportedEncodingException 
   */
  // /calamari/bitstream/handle/10738/120/BDC346.pdf%20filibusterismo.pdf.txt?sequence=1&isAllowed=y
  public static String getBitstreamData(String bitstreamParam) throws  UnsupportedEncodingException{
      	
      	String resultado = null;
      	Context context=null;
      	try {
	    
      	String handle=null;
      	int sequence=-1;
      	String name=null;
      	
      	if(bitstreamParam.contains("/handle/")){
      	   String cadena=java.net.URLDecoder.decode(bitstreamParam.substring(bitstreamParam.indexOf("/handle/")+"/handle/".length()), "UTF-8");
      	   String[] cadenaSplit=cadena.split("/");
      	   handle = cadenaSplit[0]+"/"+cadenaSplit[1];
      	   String[] paramsSplit=cadenaSplit[2].split("[?]|[&]");
      	   name= paramsSplit[0];
      	   sequence = Integer.parseInt(paramsSplit[1].substring(paramsSplit[1].indexOf("=")+1));
      	}
	    //	            
	    // Get our parameters that identify the bitstream
	 //   itemID = par.getParameterAsInteger("itemID", -1);
	//    bitstreamID = par.getParameterAsInteger("bitstreamID", -1);
	 	        
	  //  this.isSpider = par.getParameter("userAgent", "").equals("spider");
	    //
	    // Resolve the bitstream
	    Bitstream bitstream = null;
	    DSpaceObject dso = null;
	    Item item=null;
		if (handle != null)
	    {
		// Reference by an item's handle.
			// Hay que limpiar la cache para que no haga cosas raras la conversion de ficheros
		context=new Context();
		dso = HandleServiceFactory.getInstance().getHandleService().resolveToObject(context, handle);
		
		if (dso instanceof Item)
		{
		    item = (Item)dso;

		    if (sequence > -1)
		    {
		    	log.debug("Se busca bitstream por secuencia. Sequence:"+sequence);
		    	bitstream = findBitstreamBySequence(item,sequence);
		    	if(bitstream!=null){
		    		log.debug("Encontrado");
		    	}
		    }
		    else if (name != null)
		    {
		    	log.debug("Se busca bitstream por secuencia. Nombre:"+name);
		    	bitstream = findBitstreamByName(item,name);
		    	if(bitstream!=null){
		    		log.debug("Encontrado");
		    	}
		    }
		}
	    }
		 // if initial search was by sequence number and found nothing,
        // then try to find bitstream by name (assuming we have a file name)
        if((sequence > -1 && bitstream==null) && name!=null)
        {
        	log.debug("Fallo la busqueda por secuencia, se busca bitstream por nombre. Nombre:"+name);
            bitstream = findBitstreamByName(item,name);
            if(bitstream!=null){
	    		log.debug("Encontrado");
	    	}
        }
	    // Was a bitstream found?
	    if (bitstream == null)
	    {
	    	log.error("Error 2: No se encuentra bitstream:"+name);
		//throw new ResourceNotFoundException("Unable to locate bitstream");
	    	return null;
	    }

//	    // Is there a User logged in and does the user have access to read it?
//	    boolean isAuthorized = AuthorizeManager.authorizeActionBoolean(context, bitstream, Constants.READ);
//	    if (item != null && item.isWithdrawn() && !AuthorizeManager.isAdmin(context))
//	    {
//		isAuthorized = false;
//		//log.debug(LogManager.getHeader(context, "view_bitstream", "handle=" + item.getHandle() + ",withdrawn=true"));
//	    }
//
//	    if (!isAuthorized)
//	    {
//		return null;
//	    }

	    //	            // Success, bitstream found and the user has access to read it.
	    // Exist the flexpaper version?
	    if(FlexpaperUtils.flexpaperMustBeGenerated(handle,name,sequence,bitstream) && isAllowed(bitstreamParam)){
	    	FlexpaperUtils.generateFlash(handle,name,sequence,bitstream);
	    }
	    
	    // Preparo el resultado

	    	String path=FlexpaperUtils.getDocumentUrl(null,null,handle,name,sequence);
		    log.debug("Preparo el resultado. path:"+path);
		    List<String> flexInfo=FlexpaperUtils.getInfoFlexFile(handle, name, sequence);
		
		if(flexInfo!=null && path!=null){
			log.debug("TamaÃ±o de flexinfo:"+flexInfo.size());
		    resultado=path+","+flexInfo.get(0)+","+flexInfo.get(1);
		    log.debug("Resultado:"+resultado);
		}else{
			log.error("Error 9. Flexinfo:"+flexInfo+"; path:"+path);
		    return null;
		}
	} catch (SQLException e) {
		log.error("Error 1: Excepcion",e);
	    e.printStackTrace();
	}finally{
		if(context!=null){
			context.abort();
		}
	}
	return resultado;
	}
  
  
/**
 * Find the bitstream identified by a sequence number on this item.
 *
 * @param item A DSpace item
 * @param sequence The sequence of the bitstream
 * @return The bitstream or null if none found.
 */
private static Bitstream findBitstreamBySequence(Item item, int sequence) throws SQLException
{
	if (item == null)
	{
		log.error("Error 4: Item es null");
	    return null;
	}

	List<Bundle> bundles = ContentServiceFactory.getInstance().getItemService().getBundles(item, "ORIGINAL");
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
	log.error("Error 5: No se encuentra bitstream");
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
private static Bitstream findBitstreamByName(Item item, String name) throws SQLException
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
	log.error("Error 10: No se encuentra bitstream");
	return null;
}
public class SwfFileFilter implements FilenameFilter{

    String start;
    public SwfFileFilter (String start){
	this.start=start;
    }
    @Override
    public boolean accept(File dir, String name) {
	if (name.startsWith(start) && name.endsWith(".swf")) {
		return true;
	} else {
		return false;
	}
    }

}
private static class StreamEater implements Runnable {

	private final InputStream stream;
	private final StringBuffer logger;


	public StreamEater(InputStream stream, StringBuffer log) {
		this.stream = stream;
		logger = log;
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(stream);
			BufferedReader br = new BufferedReader(isr);
			String line = br.readLine();
			while (line != null) {
				logger.append(line);
				logger.append("\n");
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			logger.append(e.getMessage());
		
		}
	}

	public static StreamEater eat(InputStream stream, StringBuffer logger) {
		StreamEater eater = new StreamEater(stream, logger);
		new Thread(eater).start();
		return eater;
	}
	}

	public static String cleanName(String name) {
		if (StringUtils.isNotEmpty(name)) {
			String cleanName;
			try {
				cleanName = Normalizer.normalize(java.net.URLDecoder.decode(name, "UTF-8"), Normalizer.Form.NFD)
						.replaceAll("[^\\p{ASCII}]", "");
				return cleanName;
			} catch (UnsupportedEncodingException e) {
				log.warn("Error en conversion de nombre en enlace flexpaper");
				e.printStackTrace();
			}
		}
		return name;
	}

}
  