package yiyuan.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import excelTool.ExcelTool;
import jinDieEntryXLS.JinDieEntryXLS;
import jinDieEntryXLS.beans.RawInfo;
import jinDieEntryXLS.beans.accountEntry.Record;
import jinDieEntryXLS.helper.TypeEnum;
import yiyuan.JinDie.JinDieRecord.JinDieRecordService;
import yiyuan.JinDie.Origin.OriginService;

import yiyuan.utils.Tool;
import yiyuan.utils.msofficetools.ExcelUtil;

@RestController
@RequestMapping("/moduleTest")
public class ExternModuleController {
	@Autowired
	OriginService originService;

	@Autowired
	JinDieEntryXLS jinDieEntryXLS;

	@Autowired
	JinDieRecordService jinDieService;

	@GetMapping("/preriod/record")
	public List<Record> getRecordInPreriod(
			@RequestParam(value = "companyName", defaultValue = "YYKJ") String companyName,
			@RequestParam(value = "begin", defaultValue = "2018-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-06-30") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

		List<yiyuan.JinDie.Origin.Origin> origins_s = originService.getInPeriod(Tool.getCurrentCompanyName(), begin, end);

		origins_s = ExcelUtil.filter(origins_s, e -> e.getType().contains(TypeEnum.Bill.value));

		List<Record> records = jinDieService.processToRecords(originService.originToSupClass(origins_s));

		// BeansToXLSTransform<Record> btx=new
		// DefaultBeansToXLSTransform<>(Record.class);
		// btx.writeToFile("C:\\Users\\pzr\\Desktop\\external-test.xls", records);

		ExcelTool<Record> xlsTool = new ExcelTool<>();
		xlsTool.writeToFile(records, "C:\\Users\\pzr\\Desktop\\external-beforeBill.xls");

		return records;
	}

	@GetMapping("/fromExcel/")
	public List<RawInfo> fromExcel(HttpServletResponse response) {
		List<RawInfo> origins2 = new ArrayList<>();
		String filePath = "D:\\work\\finace\\"+Tool.getCurrentCompanyName()+"\\"+Tool.getCurrentCompanyName()+"_origin .xlsx";
		File file = new File(filePath);
		if (!file.getName().contains(Tool.getCurrentCompanyName())) {
			PrintWriter pw;
			try {
				pw = response.getWriter();
				pw.write("error! the file:" + file.getName() + " seems not like the current username:"
						+ Tool.getCurrentCompanyName());
				pw.flush();
				//pw.close();
				throw new Exception("file name not match the current name!");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			List<yiyuan.JinDie.Origin.Origin> origins = originService.getFromFilePath(filePath,
					Tool.getCurrentCompanyName());

			
			for (yiyuan.JinDie.Origin.Origin origin : origins) {
				origins2.add((RawInfo) origin);
			}

			// getRecordInPreriod("YYKJ", LocalDate.of(2019, 1, 1), LocalDate.of(2020, 5,
			// 31));

			List<Record> records = jinDieService.processToRecordsEechAccountPeriod(LocalDate.of(2020, 1, 1),
					LocalDate.of(2020, 6, 30), origins2);

			ExcelTool<Record> xlsTool = new ExcelTool<>();

			xlsTool.writeToFile(records, "C:\\Users\\pzr\\Desktop\\external-fullRecord.xls");
		}



		return origins2;
	}

	@GetMapping("/toCost/record")
	public List<Record> getCostRecordInPreriod(
			@RequestParam(value = "companyName", defaultValue = "YYKJ") String companyName,
			@RequestParam(value = "begin", defaultValue = "2018-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-06-30") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
			@RequestParam(value = "rate", defaultValue = "1") double rate) {

		List<Record> records = jinDieService.processToCostRecord(begin, end, Tool.getCurrentCompanyName(), rate);

		// BeansToXLSTransform<Record> btx=new
		// DefaultBeansToXLSTransform<>(Record.class);
		// btx.writeToFile("C:\\Users\\pzr\\Desktop\\external-test.xls", records);

		ExcelTool<Record> xlsTool = new ExcelTool<>();
		xlsTool.writeToFile(records, "C:\\Users\\pzr\\Desktop\\external-toCost.xls");

		return records;
	}

	@GetMapping("/readExpense")
	public List<RawInfo> readExpenseOriginFromFile() {

		File file = new File("D:\\work\\finace\\"+Tool.getCurrentCompanyName()+"\\费用数据录入.txt");
		List<RawInfo> origins = originService.getExpenseOriginFromFile(file, Tool.getCurrentCompanyName());

		ExcelTool<RawInfo> excelTool=new ExcelTool<>();
		excelTool.writeToFile(origins, "C:\\Users\\pzr\\Desktop\\"+Tool.getCurrentCompanyName()+"-expenseRecord.xls");
		
		

		/*
		 * List<Record> records = jinDieEntryXLS.produceRecords(origins,
		 * TypeEnum.Expense.value); ExcelTool<Record> record_xlsTool = new
		 * ExcelTool<>(); record_xlsTool.writeToFile(records,
		 * "C:\\Users\\pzr\\Desktop\\"+Tool.getCurrentCompanyName()+"-expenseRecord.xls"
		 * );
		 */

		return origins;

	}

}
