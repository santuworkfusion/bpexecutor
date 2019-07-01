package workfusion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExtractParamConfig {
	
	public static List<ReportRow> readMainConfig(String bpConfigPaths) throws InvalidFormatException, FileNotFoundException, IOException {
		List<ReportRow> listOfDataFromReport = null;
		if(bpConfigPaths!=null && !"".equals(bpConfigPaths)) {
			Workbook workbook = WorkbookFactory.create(new FileInputStream(bpConfigPaths));

			Sheet sheet = workbook.getSheetAt(0);
			int totalRows = sheet.getPhysicalNumberOfRows();

			Map<String, Integer> map = new HashMap<String, Integer>(); // Create map
			Row row = sheet.getRow(0); // Get first row
			// following is boilerplate from the java doc
			short minColIx = row.getFirstCellNum(); // get the first column index for a row
			short maxColIx = row.getLastCellNum(); // get the last column index for a row
			for (short colIx = minColIx; colIx < maxColIx; colIx++) { // loop from first to last index
				Cell cell = row.getCell(colIx); // get the cell
				map.put(cell.getStringCellValue(), cell.getColumnIndex()); // add the cell contents (name of column) and
																			// cell index to the map
			}

			listOfDataFromReport = new ArrayList<ReportRow>();
			for (int x = 1; x < totalRows; x++) {
				ReportRow rr = new ReportRow(); // Data structure to hold the data from the xls file.
				Row dataRow = sheet.getRow(x); // get row 1 to row n (rows containing data)

				int idxForColumn1 = map.get("BP_CATEGORY"); // get the column index for the column with header name = "Column1"
				int idxForColumn2 = map.get("DEFINITION_UUID"); // get the column index for the column with header name = "Column2"
				int idxForColumn3 = map.get("INPUT_CSV_PATH"); // get the column index for the column with header name = "Column3"
				int idxForColumn4 = map.get("EXPECTED_STATUS"); // get the column index for the column with header name = "Column2"

				
				Cell cell1 = dataRow.getCell(idxForColumn1); // Get the cells for each of the indexes
				Cell cell2 = dataRow.getCell(idxForColumn2);
				Cell cell3 = dataRow.getCell(idxForColumn3);
				Cell cell4 = dataRow.getCell(idxForColumn4);

				rr.setBpCategory(cell1.getStringCellValue()); // Get the values out of those cells and put them into the report
				rr.setDefinitionUUID(cell2.getStringCellValue());
				rr.setInputCsvPath(cell3.getStringCellValue());
				rr.setExpectedStatus(cell4.getStringCellValue());

				listOfDataFromReport.add(rr);

			}
		} else {
			System.err.println("Please provide the parameters in an .xlsx file");
		}
		
		return listOfDataFromReport;
		
	}
}

class ReportRow {
	private String bpCategory;
	private String definitionUUID;
	private String inputCsvPath;
	private String expectedStatus;

	public String getBpCategory() {
		return bpCategory;
	}
	public void setBpCategory(String bpCategory) {
		this.bpCategory = bpCategory;
	}
	public String getDefinitionUUID() {
		return definitionUUID;
	}
	public void setDefinitionUUID(String definitionUUID) {
		this.definitionUUID = definitionUUID;
	}
	public String getInputCsvPath() {
		return inputCsvPath;
	}
	public void setInputCsvPath(String inputCsvPath) {
		this.inputCsvPath = inputCsvPath;
	}
	public String getExpectedStatus() {
		return expectedStatus;
	}
	public void setExpectedStatus(String expectedStatus) {
		this.expectedStatus = expectedStatus;
	}
}
