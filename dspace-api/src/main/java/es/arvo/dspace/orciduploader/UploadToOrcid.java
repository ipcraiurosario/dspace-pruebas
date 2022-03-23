package es.arvo.dspace.orciduploader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.curate.AbstractCurationTask;
import org.dspace.curate.Curator;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.storage.rdbms.DB;

import com.amazonaws.services.s3.transfer.Upload;

import es.arvo.dspace.orciduploader.model.WorkExternalIdentifiersOrcidUploader;
import es.arvo.dspace.orciduploader.model.WorkOrcidUploader;
import es.arvo.dspace.orciduploader.model.WorkTypeOrcidUploader;
import es.arvo.dspace.orciduploader.model.WorksBuilderOrcidUploader;
import sun.misc.PerformanceLogger;

public class UploadToOrcid extends AbstractCurationTask {

	private Logger log = Logger.getLogger(UploadToOrcid.class);
	
	private String API_VERSION;
	private String PETITION_GET;
	private String API_URL;
	
	private Map<String, String> apiCredentials;
	
	private ItemService itemService;
	private ConfigurationService configurationService;
	
	public static int executeFromJS(String[] handles) {
		int response = Curator.CURATE_FAIL;
		try {
			if (handles != null)
				for (String handle : handles) {
					DSpaceObject dso = HandleServiceFactory.getInstance().getHandleService()
							.resolveToObject(Curator.curationContext(), handle);
					response = new UploadToOrcid().perform(dso);
					if(response == Curator.CURATE_FAIL)
						return response;
				}
			
			return response;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	@Override
	public int perform(DSpaceObject dso) throws IOException {
		if (dso.getType() == Constants.ITEM) {
			try {
				Item item = (Item) dso;
				init();

				String xml = generateWorkXML(item);
				if(checkIfNullOrEmpty(xml))
					return Curator.CURATE_FAIL;

				System.out.println(xml);
				log.info(xml);

				String postURL = createPostURL();
				checkIfNullOrEmpty(postURL);
				
				postWork(xml, postURL);
			} catch (Exception e) {
				log.error(e.getMessage());
				return Curator.CURATE_FAIL;
			}
		}

		return Curator.CURATE_SUCCESS;
	}

	/**
	 * Check if the param is null or empty
	 * @param o String 
	 * @return true if null or empty
	 */
	private boolean checkIfNullOrEmpty(String o) {
		if(o == null & o.isEmpty())
			return true;
		return false;
	}

	private void init() {
		itemService = ContentServiceFactory.getInstance().getItemService();
		configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
		
		API_VERSION = configurationService.getProperty("orcid.api.version");
		PETITION_GET = configurationService.getProperty("orcid.api.petition");
		API_URL = configurationService.getProperty("orcid.api.url.request");
	}

	private void postWork(String xml, String postURL)
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpEntity httpEntity = new ByteArrayEntity(xml.getBytes("UTF-8"));

		HttpPost httpPost = new HttpPost(postURL);
		httpPost.setHeader("Authorization", "Bearer " + apiCredentials.get("access_token").trim());
		httpPost.addHeader("Content-type", "application/vnd.orcid+xml");
		httpPost.setEntity(httpEntity);
		HttpResponse responseUpload = httpClient.execute(httpPost);

		HttpEntity result = responseUpload.getEntity();

		System.out.println(responseUpload.toString());
		log.info(responseUpload.toString());
	}
	
	private String createPostURL(){
		String url = "";
		
		if (checkIfNullOrEmpty(API_URL) || checkIfNullOrEmpty(API_VERSION) || checkIfNullOrEmpty(PETITION_GET))
			return url;
			
		url = API_URL + "/" + API_VERSION
				+ "/" + apiCredentials.get("orcid").trim() + "/" + PETITION_GET;
		
		return url;
	}
	
	private String generateWorkXML(Item item) throws SQLException{
		List<MetadataValue> workTitles = itemService.getMetadataByMetadataString(item, "dc.title");
		List<MetadataValue> workShortDescriptions = itemService.getMetadataByMetadataString(item, "dc.description.abstract");
		List<MetadataValue> workLanguageCodes = itemService.getMetadataByMetadataString(item, "dc.language.iso");
		
		String workTitle = (String) (workTitles.size() > 0? workTitles.get(0).getValue(): "");
		String workShortDescription = (String) (workShortDescriptions.size() > 0? workShortDescriptions.get(0).getValue(): "");
		String workLanguageCode = (String) (workLanguageCodes.size() > 0? workLanguageCodes.get(0).getValue(): "");

		String workExternalIdentifier = item.getHandle();

		// dc.title.alternative titlo traducidos?
		// dc.coverage.spatial se guarda en formato Ciudad (País)

		String epersonEmail = item.getSubmitter().getEmail();
		getAPICredentials(epersonEmail);

		// Obligatorio
		WorkOrcidUploader work = new WorkOrcidUploader(workTitle, WorkTypeOrcidUploader.BOOK);

		// Obligatorio
		work.setWorkExternalIdentifiers(WorkExternalIdentifiersOrcidUploader.SOURCE_WORK_ID,
				workExternalIdentifier, configurationService.getProperty("dspace.url"));

		// Opcional
		// work.setCountry("US");

		// Opcional
		// work.setJournalTitle("Journal Title");

		// Opcional
		if (!workLanguageCode.equalsIgnoreCase(""))
			work.setLanguageCode(workLanguageCode);

		// Opcional
		// work.setPublicationDate("2010", "11");

		// Opcional
		if (!workShortDescription.equals(""))
			work.setShortDescription(workShortDescription);
		
		// Opcional
		work.setUrl(configurationService.getProperty("dspace.url") + "/handle/" + workExternalIdentifier);

		// Opcional
		// work.setWorkCitation("bibtex", "@article {ORCIDtest2014,
		// author = \"Lastname, Firstname\", title = \"API Test Title\",
		// journal = \"Journal Title\", volume = \"25\", number = \"4\",
		// year = \"2010\", pages = \"259-264\", doi =
		// \"doi:10.1087/20120404\" }");

		// Opcional
		// work.setWorkContributors("http://orcid.org/0000-0001-5109-3700",
		// "LastName, FirstName", "additional", "author");

		// Opcional
		// work.setWorkTitle("My Subtitle");

		// Opcional
		// Map<String, String> translatedTitle = new HashMap<String,String>(); translatedTitle.put("fr", "API essai titre" ); 
		// work.setWorkTitle(translatedTitle);
		 

		List<WorkOrcidUploader> works = new ArrayList<WorkOrcidUploader>();
		works.add(work);

		WorksBuilderOrcidUploader worksBuilder = new WorksBuilderOrcidUploader(works);

		return worksBuilder.toString();
	}

	private void getAPICredentials(String epersonEmail) throws SQLException {
		DB db = DB.getInstance();
		apiCredentials = db.getOrcidID(epersonEmail);
	}

}
