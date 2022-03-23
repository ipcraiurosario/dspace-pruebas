/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 * 
 * Original Concept, JSPUI version:    Universidade do Minho   at www.uminho.pt
 * Sponsorship of XMLUI version:    Instituto Oceanográfico de España at www.ieo.es
 */
package org.dspace.app.xmlui.aspect.eperson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.orcid.ns.OrcidMessage;

import com.google.gson.Gson;

import es.arvo.orcid.OrcidAccessResponseVO;
import es.arvo.orcid.OrcidHelper;
import es.arvo.orcid.OrcidVO;

/**
 * 
 * @author Adán Román Ruiz at arvo.es
 */
public class OrcidAccessAction extends AbstractAction {
	/** log4j log */
	private static Logger log = Logger.getLogger(OrcidAccessAction.class);

	public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
			Parameters parameters) throws Exception {
		Request request = ObjectModelHelper.getRequest(objectModel);
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		String error = request.getParameter("error");
		if (code != null) {
			Gson gson = new Gson();

			String curlResponse = new String(executeAccessTokenRequest(code).getBytes("ISO-8859-1"),"UTF-8");
			/*
			 * {"access_token":"c47026d9-90bf-4480-a259-a953bc103495",
			 * "token_type":"bearer","expires_in":631138517,"scope":
			 * "/orcid-works/read-limited","orcid":"0000-0003-1495-7122",
			 * "name":"ORCID Test"}
			 */
			String bodyAsString = curlResponse;// EntityUtils.toString(curlResponse.getEntity());
			es.arvo.orcid.OrcidAccessResponseVO orcidAccessResponse = gson.fromJson(bodyAsString,
					es.arvo.orcid.OrcidAccessResponseVO.class);
			if (orcidAccessResponse.getErrorDesc() != null) {
				throw new Exception(orcidAccessResponse.getErrorDesc().getContent());
			}
			String accessToken = orcidAccessResponse.getAccessToken();
			String orcidId = orcidAccessResponse.getOrcid();
			System.out.println(curlResponse);
			curlResponse = new String(executeProfileRequest(orcidId, accessToken).getBytes("ISO-8859-1"),"UTF-8");

			JAXBContext jc = JAXBContext.newInstance(OrcidMessage.class);
			Unmarshaller u = jc.createUnmarshaller();
			OrcidMessage profile = (OrcidMessage) u.unmarshal(new StringReader(curlResponse));

			if (profile.getOrcidProfile() != null && profile.getOrcidProfile().getOrcidBio() != null
					&& profile.getOrcidProfile().getOrcidBio().getPersonalDetails() != null) {
				OrcidVO person = OrcidHelper.buscarPersona(profile.getOrcidProfile().getOrcidBio().getPersonalDetails(),
						state);
				if (person != null) {
					person.setOrcidId(orcidId);
					person.setOrcidName(orcidAccessResponse.getName());
					person.setAccessToken(orcidAccessResponse.getAccessToken());
					person.setTokenType(orcidAccessResponse.getTokenType());
					person.setTokenExpiration(orcidAccessResponse.getExpiresIn());
					person.setAceptationDate(new Date());
					OrcidHelper.actualizaAutor(person);
					return null;
				} else {
					OrcidHelper.mandarCorreoGestionManual(orcidAccessResponse, profile);// mandamos
																						// correo
																						// para
																						// gestion
																						// manual
					return null;
				}
			}
			OrcidHelper.mandarCorreoGestionManual(orcidAccessResponse, profile);// mandamos
																				// correo
																				// para
																				// gestion
																				// manual
			return null;
		} else if (error != null && error.equals("access_denied")) {
			OrcidVO person = OrcidHelper.buscarPersona(null, state);
			if (person != null) {
				person.setAceptationDate(new Date());
				OrcidHelper.actualizaAutor(person);
			}
		}
		return null;
	}

	private String executeProfileRequest(String orcidId, String accessToken) throws IOException {
		// curl -H "Content-Type: application/vdn.orcid+xml" -H "Authorization:
		// Bearer 693ecca5-ec1a-4084-9d40-7df5e75c2be6" -X GET
		// "https://api.sandbox.orcid.org/v1.2/0000-0002-5539-6343/orcid-profile"
		// -L -i

		// curl_init and url
		URL url = new URL(
				ConfigurationManager.getProperty("orcid.api.url.request") + "/v1.2/" + orcidId + "/orcid-profile");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// CURLOPT_POST
		con.setRequestMethod("GET");

		// CURLOPT_FOLLOWLOCATION
		con.setInstanceFollowRedirects(true);

		con.setRequestProperty("Content-Type", "application/vdn.orcid+xml");
		con.setRequestProperty("Authorization", "Bearer " + accessToken);

		con.setDoOutput(true);
		con.setDoInput(true);

		// read the response
		DataInputStream input = new DataInputStream(con.getInputStream());
		int c;
		StringBuilder resultBuf = new StringBuilder();
		while ((c = input.read()) != -1) {
			resultBuf.append((char) c);
		}
		input.close();

		return resultBuf.toString();
	}

	private Map getError(OrcidAccessResponseVO curlResponse) {
		Map map = new HashMap();
		map.put("message", curlResponse.getErrorDesc());
		return map;
	}

	private String executeAccessTokenRequest(String code) throws IOException {
		// curl -i -L -H 'Accept: application/json' --data
		// 'client_id=APP-674MCQQR985VZQ2Z&client_secret=5f63d1c5-3f00-4fa5-b096-fd985ffd0df7&grant_type=authorization_code&code=Q70Y3A&redirect_uri=https://developers.google.com/oauthplayground'
		// 'https://api.sandbox.orcid.org/oauth/token'

		// curl_init and url
		URL url = new URL(ConfigurationManager.getProperty("orcid.url.request") + "/oauth/token");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// CURLOPT_POST
		con.setRequestMethod("POST");

		// CURLOPT_FOLLOWLOCATION
		con.setInstanceFollowRedirects(true);

		con.setRequestProperty("Accept", "application/json");

		String postData = OrcidHelper.construirDataUrlToken(code);
		con.setRequestProperty("Content-length", String.valueOf(postData.length()));

		con.setDoOutput(true);
		con.setDoInput(true);

		DataOutputStream output = new DataOutputStream(con.getOutputStream());
		output.writeBytes(postData);
		output.close();

		// read the response
		DataInputStream input = new DataInputStream(con.getInputStream());
		int c;
		StringBuilder resultBuf = new StringBuilder();
		while ((c = input.read()) != -1) {
			resultBuf.append((char) c);
		}
		input.close();

		return resultBuf.toString();
	}
}
