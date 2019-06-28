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

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

//Not a production grade implementation use as an example only
public class APISample {

    private HttpClient httpClient;

    public APISample() {
        httpClient = HttpClients.createDefault();
    }

    public static void main(String... args) throws IOException {
    	String baseURL = args[0];
    	String definitionUUID = args[1];
    	String username = args[2];
    	String password = args[3];
    	String inputData = args[4];
    	
        APISample apiSample = new APISample();
        apiSample.login(baseURL+ "/dologin", username, password);

        //get version details and alike
        String serviceInfo = apiSample.get(baseURL + "/api/v2/workfusion/service/info");

        //launch new business process
        String uuid = apiSample.startBusinessProcess(baseURL, definitionUUID, inputData);
        System.out.println("BP UUID: "+uuid);
        System.out.println("Business Process Status: "+apiSample.getBusinessProcessStatus(baseURL, uuid));
        System.out.println("Service Info: "+serviceInfo);
        
        
    }
    
    /**
     * 
     * @param inputFileCSV
     * @return
     */
    public static String readCSV(String inputFileCSV) {
        String line = "";
        StringBuilder finalStr = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileCSV))) {
            while ((line = br.readLine()) != null) {
            	finalStr.append(line+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalStr.toString();
    }
    
    /**
     * 
     * @param is
     * @return
     */
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * 
     * @param addressURL
     * @return
     * @throws IOException
     */
    public String get(String addressURL) throws IOException {

        HttpGet httpGet = new HttpGet(addressURL);
        System.out.println("GET -> " + addressURL);

        HttpResponse response = httpClient.execute(httpGet);
        return convertStreamToString(response.getEntity().getContent());
    }

    /**
     * 
     * @param addressURL
     * @param body
     * @return
     * @throws IOException
     */
    public String post(String addressURL, AbstractHttpEntity body) throws IOException {

        HttpPost httpPost = new HttpPost(addressURL);
        System.out.println("POST -> " + addressURL);
        httpPost.setEntity(body);

        HttpResponse response = httpClient.execute(httpPost);
        String stringResponse = convertStreamToString(response.getEntity().getContent());
        System.out.println(stringResponse);

        return stringResponse;
    }

    /**
     * 
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
     * 
     * @param baseURL
     * @param uuid
     * @param inputData
     * @return
     * @throws IOException
     */
    public String startBusinessProcess(String baseURL, String uuid, String inputData) throws IOException {
        TaskStart taskStart = new TaskStart();
        taskStart.setCampaignUuid(uuid);

        //pass CSV file content here to provide input for a business process
        taskStart.setMainData(inputData);

        StringEntity body = new StringEntity(new Gson().toJson(taskStart));

        //the server may return error if you do not Content-Type=application/json
        body.setContentType("application/json");

        return post(baseURL + "/api/v2/workfusion/task/file", body);
    }

    /**
     * 
     * @param baseURL
     * @param uuid
     * @return
     * @throws IOException
     */
    public String getBusinessProcessStatus(String baseURL, String uuid) throws IOException {
        return get(baseURL + "/api/v2/workfusion/task/" + uuid);
    }

    /**
     * 
     * @author Santu
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
