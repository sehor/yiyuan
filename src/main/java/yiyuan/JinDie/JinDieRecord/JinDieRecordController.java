package yiyuan.JinDie.JinDieRecord;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Api(tags = "JinDieRecord 接口测试")
@RestController
@RequestMapping("/JinDieRecord")
public class JinDieRecordController {
	@Autowired
	JinDieRecordService service;

	@PostMapping("/add")
	public JinDieRecord add(@RequestBody JinDieRecord jinDieRecord) {

		return service.addJinDieRecord(jinDieRecord);
	}

	@GetMapping("/get/{id}")
	public JinDieRecord getJinDieRecord(@PathVariable(value = "id") String id) {
		return service.getJinDieRecord(id);
	}

	@PutMapping("/update")
	public JinDieRecord update(@RequestBody JinDieRecord jinDieRecord) {
		return service.updateJinDieRecord(jinDieRecord);
	}

	@DeleteMapping("/delete/{id}")
	public String delete(@PathVariable("id") String id) {

		service.deleteJinDieRecord(id);
		return "delete jinDieRecord by id :" + id;
	}
}