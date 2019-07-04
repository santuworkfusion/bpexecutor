package workfusion;

import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 * 
 * @author Santu Das
 *
 */
public class Main {

	public static void main(String... args) throws IOException, InvalidFormatException, InterruptedException {
		String baseURL = args[0];
		String username = args[1];
		String password = args[2];
		String bpConfigCSV = args[3];

		CommonUtil util = new CommonUtil();
		util.login(baseURL + "/dologin", username, password);

		List<ReportRow> configList = ExtractParamConfig.readMainConfig(bpConfigCSV);

		if (configList != null && configList.size() > 0) {
			for (int j = 0; j < configList.size(); j++) {
				System.out.println("\n##################\nSTARTING BP. Definition UUID: "
						+ configList.get(j).getDefinitionUUID());
				System.out.println("EXPECTED_STATUS: " + configList.get(j).getExpectedStatus());
				// launch new business process
				String uuid = util.startBusinessProcess(baseURL, configList.get(j).getDefinitionUUID(),
						CommonUtil.readCSV(configList.get(j).getInputCsvPath()));
				System.out.println("BP UUID: " + uuid);
				
				String bpExecutionResultJSON = util.getBusinessProcessStatus(baseURL, uuid);
				String actualBPExecutionStatus = util.getBPStatus(bpExecutionResultJSON);
				String expectedBPStatus = configList.get(j).getExpectedStatus();
				while(actualBPExecutionStatus!=null && !actualBPExecutionStatus.equalsIgnoreCase(expectedBPStatus)) {
					bpExecutionResultJSON = util.getBusinessProcessStatus(baseURL, uuid);
					actualBPExecutionStatus = util.getBPStatus(bpExecutionResultJSON);
					System.out.println("Waiting for status to be '"+expectedBPStatus+"' as provided in input config file. Current status: "+actualBPExecutionStatus);
				}
				System.out.println("Business Process Status: " + bpExecutionResultJSON);
				String expectationAchieved = (expectedBPStatus.equalsIgnoreCase(actualBPExecutionStatus))? "TRUE": "FALSE";
				System.out.println("EXPECTATION ACHIEVED: " + expectationAchieved);
			}
		}
	}

}
