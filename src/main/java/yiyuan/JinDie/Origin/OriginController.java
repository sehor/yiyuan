package yiyuan.JinDie.Origin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

import excelTool.ExcelTool;
import io.swagger.annotations.Api;
import jinDieEntryXLS.beans.accountEntry.Record;
import yiyuan.JinDie.OriginType;

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

	@Autowired
	MongoOperations operations;

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
		return "done!delete by id: " + id;
	}

	@DeleteMapping("/delete")
	public String deleteCompanyOrigins(@PathParam(value = "companyName") String companyName) {
		List<Origin> origins = operations.find(Query.query(Criteria.where("companyName").is(companyName)),
				Origin.class);
		for (Origin origin : origins) {
			service.deleteOrigin(origin);
		}
		return "done";
	}

	@GetMapping("/all/{companyName}")
	public List<Origin> getAll(@PathVariable("companyName") String companyName) {
		List<Origin> list = service.getAll();
		list = ExcelUtil.filter(list, e -> e.getCompanyName().equals(companyName));
		// list=ExcelUtil.filter(list, e->e.getType().contains(OriginType.BanK.value));
		return list;
	}

	@GetMapping("/operations")
	public List<Origin> operations() {

		List<Origin> list = new ArrayList<>();
		LocalDate begin = LocalDate.of(2020, 01, 01);
		LocalDate end = LocalDate.of(2020, 02, 28);
		String typeStr = "ank";
		Query query = Query.query(Criteria.where("type").regex(typeStr).and("companyName").is("YYKJ")
				.and("occur_date").gte(begin).lte(end));

		list = operations.find(query, Origin.class);

		return list;
	}

	@GetMapping("/fromExcel")
	public List<Origin> fromExcel(HttpServletResponse response) {
		String filePath = "D:\\work\\finace\\" + Tool.getCurrentCompanyName() + "\\" + Tool.getCurrentCompanyName()
				+ "_origin.xlsx";
		File file = new File(filePath);
		if (!file.getName().contains(Tool.getCurrentCompanyName())) {
			PrintWriter pw;
			try {
				pw = response.getWriter();
				pw.write("error! the file:" + file.getName() + " seems not like the current username:"
						+ Tool.getCurrentCompanyName());
				pw.flush();
				pw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<Origin> list = service.getFromFilePath(filePath, Tool.getCurrentCompanyName());

		service.saveAll(list);
		return list;
	}

	@GetMapping("/preriod/origin")
	public List<Origin> getOriginInPreriod(@RequestParam(value = "companyName") String companyName,
			@RequestParam(value = "begin", defaultValue = "2019-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-12-31") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

		List<Origin> origins = service.getInPeriod(companyName, begin, end);

		origins = process.setCompanyName(companyName).preProcessOrigin(origins);

		return origins;
	}

	@GetMapping("/preriod/record")
	public List<Record> getRecordInPreriod(@RequestParam(value = "typeString", defaultValue = "") String typeString,
			@RequestParam(value = "begin", defaultValue = "2019-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-01-31") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

		List<Record> records = recordService.processToRecordsFromDataBase(begin, end, Tool.getCurrentCompanyName(),
				typeString);

		String path = "D:\\work\\finace\\" + Tool.getCurrentCompanyName() + "\\";
		String fileName = Tool.getCurrentCompanyName() + "_record.xlsx";

		new ExcelTool<Record>().writeToFile(records, path + fileName);

		return records;
	}

	@GetMapping("/preriod/salary")
	public double getSalaryInPreriod(@RequestParam(value = "companyName", defaultValue = "TADKJ") String companyName,
			@RequestParam(value = "begin", defaultValue = "2019-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-01-31") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

		return service.findSalary(companyName, begin, end);
	}

	@GetMapping("/preriod/security")
	public double getSecurityInPreriod(@RequestParam(value = "companyName", defaultValue = "TADKJ") String companyName,
			@RequestParam(value = "begin", defaultValue = "2019-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-01-31") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

		return service.findPersonSecurity(companyName, begin, end);
	}

	@GetMapping("/preriod/fund")
	public double getFundInPreriod(@RequestParam(value = "companyName", defaultValue = "TADKJ") String companyName,
			@RequestParam(value = "begin", defaultValue = "2019-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2019-01-31") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

		return service.findPersonFund(companyName, begin, end);
	}

	@GetMapping("/account")
	public Collection<String> getRelativeAccount(@PathParam("companyName") String companyName,
			@PathParam("type") String type) {
		List<Origin> origins = ExcelUtil.filter(service.getAll(),
				e -> e.getCompanyName().equals(companyName) && e.getType() != null && e.getType().contains(type));

		Collection<String> set = new HashSet<>();
		origins.forEach(e -> set.add(e.getRelative_account()));

		return set;
	}

}