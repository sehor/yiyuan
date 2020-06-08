package yiyuan.JinDie.Origin;

import io.swagger.annotations.Api;
import yiyuan.JinDie.OriginType;
import yiyuan.JinDie.JinDieRecord.JinDieRecord;
import yiyuan.JinDie.JinDieRecord.JinDieRecordService;
import yiyuan.utils.msofficetools.DefaultBeansToXLSTransform;
import yiyuan.utils.msofficetools.OriginProcess;
import yiyuan.utils.msofficetools.ReadDataFromExcel;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

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

	@DeleteMapping("/delete/{companyName}")
	public List<Origin> delete(@PathVariable("companyName") String companyName) {

		List<Origin> list = service.deleteByCompanyName(companyName);
		return list;
	}

	@GetMapping("/all")
	public List<Origin> getAll() {
		List<Origin> list = service.getAll();
		return list;
	}

	@GetMapping("/operations")
	public List<Origin> operations() {
		List<Origin> list = service.getAll();
		list = list.stream().filter(
				e -> e.getCompanyName().equals("CHKJ") && e.getType().equalsIgnoreCase(OriginType.Issue_Invoice.value))
				.collect(Collectors.toList());

		return list;
	}

	@GetMapping("/fromExcel")
	public List<Origin> fromExcel() {
		List<Origin> list = readDataFromExcel.readWorkbook("CHKJ", new File("C:\\Users\\pzr\\Desktop\\创和origin .xlsx"));
		service.saveAll(list);
		return list;
	}

	@GetMapping("/preriod/origin")
	public List<Origin> getOriginInPreriod(@RequestParam(value = "name", defaultValue = "CHKJ") String companyName,
			@RequestParam(value = "begin", defaultValue = "2018-09-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2018-09-30") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

		List<Origin> origins = service.getInPeriod(companyName, begin, end);
		origins = origins.stream().filter(e -> e.getType().contains("Bank")).collect(Collectors.toList());
		origins = process.setCompanyName(companyName).preProcessOrigin(origins);

		try {
			new DefaultBeansToXLSTransform<Origin>(Origin.class).createWorkbook(origins)
					.write(new File("C:\\Users\\pzr\\Desktop\\preOrigin-创和.xlsx"));
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}

		return origins;
	}

	@GetMapping("/preriod/record")
	public List<JinDieRecord> getRecordInPreriod(
			@RequestParam(value = "name", defaultValue = "CHKJ") String companyName,
			@RequestParam(value = "begin", defaultValue = "2018-09-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
			@RequestParam(value = "end", defaultValue = "2018-09-30") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

		List<JinDieRecord> records = recordService.processToRecords(begin, end, companyName, OriginType.BanK.value);

		process.recordWriteToFile("C:\\Users\\pzr\\Desktop\\record-创和.xlsx", records);

		return records;
	}

}