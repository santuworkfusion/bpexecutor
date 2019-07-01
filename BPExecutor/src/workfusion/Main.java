package workfusion;

import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Main {

	public static void main(String... args) throws IOException, InvalidFormatException {
		String baseURL = args[0];
		// String definitionUUID = args[1];
		String username = args[1];
		String password = args[2];
		String bpConfigCSV = args[3];

		CommonUtil util = new CommonUtil();
		util.login(baseURL + "/dologin", username, password);

		// String serviceInfo = apiSample.get(baseURL +
		// "/api/v2/workfusion/service/info");

		List<ReportRow> listOfDataFromReport = ExtractParamConfig.readConfig(bpConfigCSV);

		// Now you have a list of report rows
		if (listOfDataFromReport != null && listOfDataFromReport.size() > 0) {
			for (int j = 0; j < listOfDataFromReport.size(); j++) {
				System.out.println("\n##################\nSTARTING BP. Definition UUID: "
						+ listOfDataFromReport.get(j).getDefinitionUUID());
				System.out.println("EXPECTED_STATUS: " + listOfDataFromReport.get(j).getExpectedStatus());
				// launch new business process
				String uuid = util.startBusinessProcess(baseURL, listOfDataFromReport.get(j).getDefinitionUUID(),
						CommonUtil.readCSV(listOfDataFromReport.get(j).getInputCsvPath()));
				System.out.println("BP UUID: " + uuid);
				
				try {
					Thread.sleep(120000);
					System.out.println("Business Process Status: " + util.getBusinessProcessStatus(baseURL, uuid));
					String expectationAchieved = (listOfDataFromReport.get(j).getExpectedStatus()
							.equalsIgnoreCase(util.getBPStatus(util.getBusinessProcessStatus(baseURL, uuid))))? "TRUE": "FALSE";
					System.out.println("EXPECTATION ACHIEVED: " + expectationAchieved);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	}

}
