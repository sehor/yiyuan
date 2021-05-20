package yiyuan.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;


import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import jinDieEntryXLS.beans.RawInfo;
import yiyuan.JinDie.OriginType;


public class VATCollector implements Visitor {

	private String currentDirname;
	private List<VATRecord> records = new ArrayList<>();

	public List<VATRecord> getRecords() {
		return records;
	}

	@Override
	public void handleFile(File file) {
		// TODO Auto-generated method stub
		if (file.getName().contains("增值税纳税申报表")) {
			HSSFWorkbook workbook = null;
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				workbook = new HSSFWorkbook(inputStream);
				HSSFSheet sheet = workbook.getSheetAt(0);
				HSSFRow row = sheet.getRow(34);
				HSSFRow row1=sheet.getRow(22);

				VATRecord record = new VATRecord();

				record.set应交税款(row.getCell(6).getNumericCellValue());
				record.set累计应交税款(row.getCell(7).getNumericCellValue());
				record.set累计进项税额( row1.getCell(7).getNumericCellValue());
				record.setDate(this.currentDirname.substring("增值税一般纳税人".length()));
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
		// TODO Auto-generated method stub
		this.currentDirname = director.getName();
	}
	
	public List<RawInfo> convertToOrigin(List<VATRecord> records){
		
		List<RawInfo> origins=new ArrayList<>();
		records.forEach(e->{		
			if(e.get应交税款()>0.01) {
				System.out.println(e.getDate()+" 应交税款："+e.get应交税款()+"   累计进项："+e.get累计进项税额()+"  累计应交税款："+e.get累计应交税款());
				RawInfo origin=new RawInfo();
				LocalDate date=LocalDate.parse(e.getDate()+"01", DateTimeFormatter.ofPattern("yyyyMMdd"));
				
				origin.setOccur_date(date.with(TemporalAdjusters.lastDayOfMonth()));
				
				origin.setType(OriginType.Handle_VTA.value);
				origin.setAmout(e.get应交税款());
	            origins.add(origin);      
			}
				
		});
		
		return origins;
	}

}
