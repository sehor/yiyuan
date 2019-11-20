package yiyuan.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import yiyuan.domain.Customer;
import yiyuan.domain.SaleRecord;
import yiyuan.service.CustomerService;
import yiyuan.service.SaleRecordService;
import yiyuan.utils.msofficetools.DefaultXLSToBeanTransform;


@RestController("/")
public class welcomeController {
@Autowired
	SaleRecordService saleRecordService;
@Autowired
	CustomerService customerService;
	
	@GetMapping
	String welcome() {
		return "welcome";
	}
	
	@GetMapping("/saleRecords")
	List<SaleRecord> getAllSaleRecords() throws IOException, ParseException {
        File file =new File("D:\\temp\\开票要求.xlsx");
        LocalDate date=LocalDate.of(2019,01,15);

		return saleRecordService.getSaleRecordsFromXLXS(file,35,date);
	}

	@GetMapping("/customers")
	List<Customer> getAllCustomers(){
		File file=new File("D:\\temp\\客户开票信息表.xlsx");
		return customerService.getAllCustomers(file);
	}
}
