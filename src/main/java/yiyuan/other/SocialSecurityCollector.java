package yiyuan.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import yiyuan.JinDie.OriginType;
import yiyuan.JinDie.Origin.Origin;
import yiyuan.utils.msofficetools.ExcelUtil;

public class SocialSecurityCollector implements Visitor {

	
	private List<Origin> records = new ArrayList<>();

	public List<Origin> getRecords() {
		return records;
	}

	@Override
	public void handleFile(File file) {
		// TODO Auto-generated method stub
		if (file.getName().contains("缴交明细")) {
			HSSFWorkbook workbook = null;
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				workbook = new HSSFWorkbook(inputStream);
				HSSFSheet sheet = workbook.getSheetAt(0);
				Origin record = new Origin();
				record.setType(OriginType.Accrued_SalaryAndSecurity.value);
				
				
				HSSFCell cell_date=sheet.getRow(2).getCell(0); //日期在第二行第0列
				String dateString=cell_date.getStringCellValue();
				Matcher matcher=Pattern.compile("\\d+").matcher(dateString);
				String date_s="";
				while(matcher.find()) {
					date_s=date_s+dateString.substring(matcher.start(),matcher.end());
				}
				record.setOccur_date(LocalDate.parse(date_s+"26",DateTimeFormatter.ofPattern("yyyyMMdd")));
				
				
				int searchColumnIndex=1;
				for(int rowIndex=25;rowIndex<=50;rowIndex++) {       //从26行开始，在第二列搜
					
					if(sheet.getRow(rowIndex)==null) continue;
					
					HSSFCell cell=sheet.getRow(rowIndex).getCell(searchColumnIndex);
					
					if(cell!=null&&ExcelUtil.getCellValueByCell(cell).contains("当月应缴合计")) { //找到数据行
						 for(int columIndex=1;columIndex<8;columIndex++) {
							 

							  HSSFCell cell1=sheet.getRow(rowIndex).getCell(columIndex);
							  
							  if(cell1!=null) {
								  String value=ExcelUtil.getCellValueByCell(cell1);
								  if(value.contains("个人应缴合计")) {
									 String dvale= value.substring("个人应缴合计：".length()).replaceAll(",", "");
									 record.setPayedPersonSecurity(Double.valueOf(value.substring("个人应缴合计：".length()).replaceAll(",", "")));
								  }else if(value.contains("单位应缴合计：")) {
										 record.setPayedCompanySecurity(Double.valueOf(value.substring("单位应缴合计：".length()).replaceAll(",", "")));
									  }
							  }
						 }
						 
						 break;
					}
					  
				}
				

				this.records.add(record);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (workbook != null)
					try {
						workbook.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				if (inputStream != null) {

					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	@Override
	public void handleDirector(File director) {
	
		
	}

}
