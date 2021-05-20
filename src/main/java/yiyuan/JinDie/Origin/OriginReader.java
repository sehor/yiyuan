package yiyuan.JinDie.Origin;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import excelTool.reader.DefaultHelperImpl;
import excelTool.reader.Reader;
import excelTool.reader.ReaderHelper;

public class OriginReader implements Reader<Origin>{

  private Workbook workbook;
  private Class<Origin> clzz;
  private String companyName;
	
  public OriginReader(Class<Origin> clzz, Workbook workbook,String companyName) {
	this.clzz=clzz;
	this.workbook=workbook;
	this.companyName=companyName;
}
	@Override
	public List<Origin> creatBeans() {
		// TODO Auto-generated method stub
		List<Origin> origins=new ArrayList<>();
		
		ReaderHelper<Origin> helper=new DefaultHelperImpl<>(workbook, this.clzz);
		
		for(int i=0;i<workbook.getNumberOfSheets();i++) {
			List<Origin> sOrigins=new ArrayList<>();
			Sheet sheet=workbook.getSheetAt(i);
			sOrigins=helper.getBeans(sheet);
			
			for(Origin origin:sOrigins) {
				String type=sheet.getSheetName().startsWith("Bank")?"Bank":sheet.getSheetName(); //多家银行，把银行编号去掉
				
				origin.setType(type); // type暂时先设为sheet名
				origin.setId(companyName + sheet.getSheetName() + origin.getSerial_number()); // 设置id 公司名+sheet名+序号	
				origin.setCompanyName(companyName);
				
				//有多家银行
				if(sheet.getSheetName().contains("Bank_")) {
					origin.setBankNum(getBankNum(sheet.getSheetName()));
				}
			}

			origins.addAll(sOrigins);
			
		}
		
		
		return origins;
	}
	
	

	private String getBankNum(String sheetName) {
		
		return sheetName.replaceAll("Bank_", "");
	}
}
