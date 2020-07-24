package yiyuan.JinDie.JinDieRecord;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "JinDieRecord 接口测试")
@RestController
@RequestMapping("/JinDieRecord")
public class JinDieRecordController {
	@Autowired
	JinDieRecordService service;


}