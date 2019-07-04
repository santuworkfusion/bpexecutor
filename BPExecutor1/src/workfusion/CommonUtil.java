package workfusion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Santu
 *
 */
public class CommonUtil {

	private HttpClient httpClient;

	public CommonUtil() {
		httpClient = HttpClients.createDefault();
	}

	/**
	 * This method read the CSV input file of BP
	 * @param inputFileCSV
	 * @return {@link String}
	 */
	public static String readCSV(String inputFileCSV) {
		String line = "";
		StringBuilder finalStr = new StringBuilder();
		if (inputFileCSV != null && !"NA".equalsIgnoreCase(inputFileCSV) && !"N/A".equalsIgnoreCase(inputFileCSV)) {
			try (BufferedReader br = new BufferedReader(new FileReader(inputFileCSV))) {
				while ((line = br.readLine()) != null) {
					finalStr.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			finalStr.append("test\ntestdata");
		}

		return finalStr.toString();
	}

	/**
	 * This method converts the stream to string
	 * @param inputStream
	 * @return {@link String}
	 */
	static String convertStreamToString(java.io.InputStream inputStream) {
		java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	/**
	 * This method invokes the HTTP GET request
	 * @param addressURL
	 * @return {@link String}
	 * @throws IOException
	 */
	public String get(String addressURL) throws IOException {

		HttpGet httpGet = new HttpGet(addressURL);
		System.out.println("GET -> " + addressURL);

		HttpResponse response = httpClient.execute(httpGet);
		return convertStreamToString(response.getEntity().getContent());
	}

	/**
	 * This method invokes the HTTP POST request
	 * @param addressURL
	 * @param body
	 * @return {@link String}
	 * @throws IOException
	 */
	public String post(String addressURL, AbstractHttpEntity body) throws IOException {

		HttpPost httpPost = new HttpPost(addressURL);
		System.out.println("POST -> " + addressURL);
		httpPost.setEntity(body);

		HttpResponse response = httpClient.execute(httpPost);
		String stringResponse = convertStreamToString(response.getEntity().getContent());
		return stringResponse;
	}

	/**
	 * This method is responsible to log in to the Control Tower using credentials.
	 * @param loginURL
	 * @param username
	 * @param password
	 * @throws IOException
	 */
	public void login(String loginURL, String username, String password) throws IOException {

		List<NameValuePair> nvp = new ArrayList<>();
		nvp.add(new BasicNameValuePair("j_username", username));
		nvp.add(new BasicNameValuePair("j_password", password));

		post(loginURL, new UrlEncodedFormEntity(nvp));
	}

	/**
	 * This method is responsible to start the BP
	 * @param baseURL
	 * @param uuid
	 * @param inputData
	 * @return {@link String}
	 * @throws IOException
	 */
	public String startBusinessProcess(String baseURL, String uuid, String inputData) throws IOException {
		TaskStart taskStart = new TaskStart();
		taskStart.setCampaignUuid(uuid);
		// pass CSV file content here to provide input for a business process
		taskStart.setMainData(inputData);
		StringEntity body = new StringEntity(new Gson().toJson(taskStart));
		// the server may return error if you do not Content-Type=application/json
		body.setContentType("application/json");
		return post(baseURL + "/api/v2/workfusion/task/file", body);
	}

	/**
	 * This method retrieves the response of BP in JSON format
	 * @param baseURL
	 * @param uuid
	 * @return {@link String}
	 * @throws IOException
	 */
	public String getBusinessProcessStatus(String baseURL, String uuid) throws IOException {
		return get(baseURL + "/api/v2/workfusion/task/" + uuid);
	}

	/**
	 * This method is responsible to get the status of BP
	 * @param jsonInput
	 * @return {@link String}
	 */
	public String getBPStatus(String jsonInput) {
		String status = "";

		try {
			JSONObject jsonObj = (JSONObject) new JSONParser().parse(jsonInput);
			status = (String) jsonObj.get("status");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * 
	 * @author Santu Das
	 *
	 */
	private static class TaskStart implements Serializable {

		@SerializedName("campaignUuid")
		private String campaignUuid;

		@SerializedName("mainData")
		private String mainData;

		@SerializedName("sandbox")
		private Boolean isSandbox;

		@SerializedName("tags")
		private String tags;

		@SerializedName("goldData")
		private String goldData;

		@SerializedName("qualificationRun")
		private Boolean qualificationRun;

		@SerializedName("qualificationTraining")
		private Boolean qualificationTraining;

		public TaskStart() {

		}

		public String getCampaignUuid() {
			return campaignUuid;
		}

		public void setCampaignUuid(String campaignUuid) {
			this.campaignUuid = campaignUuid;
		}

		public String getMainData() {
			return mainData;
		}

		public void setMainData(String mainData) {
			this.mainData = mainData;
		}

		public Boolean isSandbox() {
			return isSandbox;
		}

		public void setSandbox(Boolean sandbox) {
			isSandbox = sandbox;
		}

		public String getTags() {
			return tags;
		}

		public Boolean getSandbox() {
			return isSandbox;
		}

		public void setTags(String tags) {
			this.tags = tags;
		}

		public String getGoldData() {
			return goldData;
		}

		public void setGoldData(String goldData) {
			this.goldData = goldData;
		}

		public Boolean getQualificationRun() {
			return qualificationRun;
		}

		public void setQualificationRun(Boolean qualificationRun) {
			this.qualificationRun = qualificationRun;
		}

		public Boolean getQualificationTraining() {
			return qualificationTraining;
		}

		public void setQualificationTraining(Boolean qualificationTraining) {
			this.qualificationTraining = qualificationTraining;
		}
	}

}
