package yiyuan.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import yiyuan.JinDie.Origin.Origin;
import yiyuan.domain.Customer;
import yiyuan.domain.CustomerSaleRecord;
import yiyuan.domain.SaleRecord;
import yiyuan.service.CustomerSaleRecordService;
import yiyuan.service.CustomerService;
import yiyuan.service.SaleRecordService;
import yiyuan.service.YiyuanService;
import yiyuan.utils.CompanyProperties;
import yiyuan.utils.TXMachinesProperties;
import yiyuan.utils.Tool;
import yiyuan.utils.msofficetools.OriginProcess;
import yiyuan.utils.msofficetools.ReadDataFromExcel;

@RestController
public class welcomeController {
	@Autowired
	SaleRecordService saleRecordService;
	@Autowired
	CustomerService customerService;
	@Autowired
	CustomerSaleRecordService customerSaleRecordService;
	@Autowired
	YiyuanService yiyuanService;

	@Autowired
	ReadDataFromExcel readDataFromExcel;
	
	@Autowired
	OriginProcess originProcess;
	
	@Autowired
	CompanyProperties companyProperties;
	
	@Autowired
	TXMachinesProperties txm;

	@GetMapping("/welcome")
	String welcome() {
		return "welcome";
	}

	@GetMapping("/saleRecords")
	List<SaleRecord> getAllSaleRecords() throws IOException, ParseException {
		File file = new File("D:\\temp\\开票要求.xlsx");
		LocalDate date = LocalDate.of(2019, 01, 15);

		return saleRecordService.getSaleRecordsFromXLXS(file, 35, date);
	}

	@GetMapping("/customers")
	List<Customer> getAllCustomers() {
		File file = new File("D:\\temp\\客户开票信息表.xlsx");
		return customerService.getAllCustomers(file);
	}

	@GetMapping("/customerSaleRecords")
	List<CustomerSaleRecord> getCustomerSaleRecords() throws IOException, ParseException {

		return customerSaleRecordService.getCustomerSaleRecords(getAllSaleRecords());
	}

	@GetMapping("/createContractFiles")
	String createContractXLSXFiles() throws IOException {
		System.out.println("begin to create files....");
		String saleFilePath = "D:\\temp\\开票要求.xlsx";
		String contractFilePath = "D:\\temp\\微电能开票购销合同样版1.xls";
		String savePath = "D:\\temp\\contract";
		LocalDate date = LocalDate.parse("2018-01-26");
		int beginSheetIndex = 20;
		for (int i = 0; i < 12; i++) {

			yiyuanService.createContractFile(saleFilePath, contractFilePath, savePath, date.plusMonths(i),
					beginSheetIndex + i);
		}
		// yiyuanService.createContractFile(saleFilePath,contractFilePath,savePath);
		return "begin to create files...";
	}

	@GetMapping("/justTest")
	List<Origin> justTest() {

		List<Origin> list = readDataFromExcel.readWorkbook("深圳市宜源科技有限公司",
				new File("C:\\Users\\pzr\\Desktop\\银行账.xlsx"));
		list=originProcess.preProcessOrigin(list);
		return list;
	}
	
	
	
	@GetMapping("/companies")
	public CompanyProperties getCompanies(){
		//System.out.println(companyProperties.getName());
		return companyProperties;
		
	}
	
	@GetMapping("/user")
	public Object userInfor() {
		return Tool.getCurrentCompanyName();
	}
}
