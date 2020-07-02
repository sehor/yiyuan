package yiyuan.JinDie.Origin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sun.net.httpserver.HttpServer;

import io.swagger.annotations.Api;
import yiyuan.JinDie.OriginType;
import yiyuan.JinDie.JinDieRecord.JinDieRecord;
import yiyuan.JinDie.JinDieRecord.JinDieRecordService;
import yiyuan.utils.Tool;
import yiyuan.utils.msofficetools.ExcelUtil;
import yiyuan.utils.msofficetools.OriginProcess;
import yiyuan.utils.msofficetools.ReadDataFromExcel;

@Api(tags = "Origin 接口测试")
@RestController
@RequestMapping("/Origin")
public class OriginController {
	@Autowired
	OriginService service;
	@Autowired
	OriginProcess process;

	@Autowired
	ReadDataFromExcel readDataFromExcel;

	@Autowired
	JinDieRecordService recordService;

	@PostMapping("/add")
	public Origin add(@RequestBody Origin origin) {

		return service.addOrigin(origin);
	}

	@GetMapping("/get/{id}")
	public Origin getOrigin(@PathVariable(value = "id") String id) {
		return service.getOrigin(id);
	}

	@PutMapping("/update")
	public Origin update(@RequestBody Origin origin) {
		return service.updateOrigin(origin);
	}

	@DeleteMapping("/delete/{id}")
	public String delete(@PathVariable("id") String id) {

		 service.deleteOrigin(id);
		return "done!delete by id: "+id;
	}

	@GetMapping("/all/{companyName}")
	public List<Origin> getAll(@PathVariable("companyName") String companyName) {
		List<Origin> list = service.getAll();
		list=ExcelUtil.filter(list, e->e.getCompanyName().equals(companyName));
		//list=ExcelUtil.filter(list, e->e.getType().contains(OriginType.BanK.value));
		return list;
	}

	@GetMapping("/operations")
	public List<Origin> operations() {
		List<Origin> list = service.getAll();
		list = list.stream().filter(
				e -> e.getCompanyName().equals("TADKJ") && e.getType().equalsIgnoreCase(OriginType.Issue_Invoice.value))
				.collect(Collectors.toList());

		return list;
	}

	@GetMapping("/fromExcel/")
	public List<Origin> fromExcel(HttpServletResponse response){
		String filePath="C:/Users/pzr/Desktop/TADKJorigin .xlsx";
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
		List<Origin> list = readDataFromExcel.readWorkbook(Tool.getCurrentCompanyName(), file);
		//service.saveAll(list);
		return list;
	}

	@GetMapping("/preriod/origin")
	public List<Origin> getOriginInPreriod(@RequestParam(value = "companyName") String companyName,
			@RequestParam(value = "begin", defaultValue = "2019-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-12-31") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

	
	
		List<Origin> origins = service.getInPeriod(companyName, begin, end);
		//origins = origins.stream().filter(e -> e.getRelative_account().contains("深圳市宜源科技有限公司的保证金")).collect(Collectors.toList());
		origins = process.setCompanyName(companyName).preProcessOrigin(origins);

		/*
		 * try { new
		 * DefaultBeansToXLSTransform<Origin>(Origin.class).createWorkbook(origins)
		 * .write(new File("C:\\Users\\pzr\\Desktop\\preOrigin-创和.xlsx")); } catch
		 * (IOException e1) {
		 * 
		 * e1.printStackTrace(); }
		 */

		return origins;
	}

	@GetMapping("/preriod/record")
	public List<JinDieRecord> getRecordInPreriod(
			@RequestParam(value = "companyName", defaultValue = "TADKJ") String companyName,
			@RequestParam(value = "begin", defaultValue = "2019-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-01-31") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

		
		List<JinDieRecord> records = recordService.processToRecords(begin, end, companyName, OriginType.Bill.value);

		process.recordWriteToFile("C:\\Users\\pzr\\Desktop\\record-泰安达.xlsx", records);

		return records;
	}
	
	
	@GetMapping("/preriod/salary")
	public double getSalaryInPreriod(
			@RequestParam(value = "companyName", defaultValue = "TADKJ") String companyName,
			@RequestParam(value = "begin", defaultValue = "2019-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-01-31") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

		return service.findSalary(companyName, begin, end);
	}
	
	@GetMapping("/preriod/security")
	public double getSecurityInPreriod(
			@RequestParam(value = "companyName", defaultValue = "TADKJ") String companyName,
			@RequestParam(value = "begin", defaultValue = "2019-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-01-31") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

		return service.findPersonSecurity(companyName, begin, end);
	}

	@GetMapping("/preriod/fund")
	public double getFundInPreriod(
			@RequestParam(value = "companyName", defaultValue = "TADKJ") String companyName,
			@RequestParam(value = "begin", defaultValue = "2019-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-01-31") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

		return service.findPersonFund(companyName, begin, end);
	}
	
	@GetMapping("/account")
	
	public Collection<String> getRelativeAccount(@PathParam("companyName") String companyName,@PathParam("type") String type){
		List<Origin> origins=ExcelUtil.filter(service.getAll(), e->e.getCompanyName().equals(companyName)&&e.getType()!=null&&e.getType().contains(type));
		
		Collection<String> set=new HashSet<>();
		origins.forEach(e->set.add(e.getRelative_account()));
		
		
		return set;
	}
	
	
}