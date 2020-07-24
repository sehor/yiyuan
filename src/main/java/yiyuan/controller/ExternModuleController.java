package yiyuan.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import excelTool.ExcelTool;
import jinDieEntryXLS.beans.Origin;
import jinDieEntryXLS.beans.accountEntry.Record;
import jinDieEntryXLS.helper.TypeEnum;
import yiyuan.JinDie.JinDieRecord.JinDieRecordService;
import yiyuan.JinDie.Origin.OriginService;
import yiyuan.externalModule.JinDieEntryDataService;
import yiyuan.utils.Tool;
import yiyuan.utils.msofficetools.ExcelUtil;


@RestController
@RequestMapping("/moduleTest")
public class ExternModuleController {
@Autowired
OriginService originService;

@Autowired
JinDieEntryDataService dataService;

@Autowired
JinDieRecordService jinDieService;


	
	@GetMapping("/preriod/record")
	public List<Record> getRecordInPreriod(
			@RequestParam(value = "companyName", defaultValue = "YYKJ") String companyName,
			@RequestParam(value = "begin", defaultValue = "2018-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-06-30") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
		

	
		List<yiyuan.JinDie.Origin.Origin> origins_s=originService.getInPeriod(companyName, begin, end);
		
		origins_s=ExcelUtil.filter(origins_s, e->e.getType().contains(TypeEnum.Bill.value));
		
		List<Record> records = jinDieService.processToRecords(originService.originToSubcalss(origins_s)) ;

       // BeansToXLSTransform<Record> btx=new DefaultBeansToXLSTransform<>(Record.class);
        //btx.writeToFile("C:\\Users\\pzr\\Desktop\\external-test.xls", records);
		
		ExcelTool<Record> xlsTool=new ExcelTool<>();
		xlsTool.writeToFile(records, "C:\\Users\\pzr\\Desktop\\external-test11.xls");

		return records;
	}
	
	@GetMapping("/fromExcel/")
	public List<Origin> fromExcel(HttpServletResponse response){
		String filePath="C:/Users/pzr/Desktop/YYKJ_origin .xlsx";
		File file=new File(filePath);
		if(!file.getName().contains(Tool.getCurrentCompanyName())) {
			PrintWriter pw;
			try {
				pw = response.getWriter();
				pw.write("error! the file:"+file.getName()+" seems not like the current username:"+Tool.getCurrentCompanyName());
				pw.flush();
				pw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<yiyuan.JinDie.Origin.Origin> origins=originService.getFromFilePath(filePath,Tool.getCurrentCompanyName());
		
        List<Origin> origins2=new ArrayList<>();
        for(yiyuan.JinDie.Origin.Origin origin:origins) {
        	origins2.add((Origin) origin);
        }
		
		//getRecordInPreriod("YYKJ", LocalDate.of(2019, 1, 1), LocalDate.of(2020, 5, 31));
        
        List<Record> records =jinDieService.processToRecordsEechAccountPeriod(LocalDate.of(2019, 1, 1), LocalDate.of(2020, 5, 31), origins2);
		
		ExcelTool<Record> xlsTool=new ExcelTool<>();
		
		xlsTool.writeToFile(records, "C:\\Users\\pzr\\Desktop\\external-test11.xls");
		
		return origins2;
	}
}



 
