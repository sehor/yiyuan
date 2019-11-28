package yiyuan.core.accountClassification;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Api(tags = "AccCla 接口测试")
@RestController
@RequestMapping("/AccCla")
public class AccClaController {
    @Autowired
    AccClaService service;

    @ApiOperation(value = "example", notes = "this is a example")
    @ApiResponse(message = "访问成功", code = 200)
    @ApiImplicitParam(value = "str", type = "path", required = false, name = "path variable")
    @GetMapping("/example/{str}")
    public String example(@PathVariable(value = "str") String str) {
        return "你好，" + str;
    }

    @PostMapping("/add")
    public AccCla add(@RequestBody AccCla accCla) {

        return service.addAccCla(accCla);
    }

    @GetMapping("/get/{id}")
    public AccCla getAccCla(@PathVariable(value = "id") String id) {
        return service.getAccCla(id);
    }

    @PutMapping("/update")
    public AccCla update(@RequestBody AccCla accCla) {
        return service.updateAccCla(accCla);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) {

        service.deleteAccCla(id);
        return "delete accCla by id :" + id;
    }

    @GetMapping("/rootPresentation")
    public AccClaPresentation getRootPresentation(){
        AccCla accCla=new AccCla();
        accCla.setId("root");
        accCla.setName("RootNode");

        return service.createAccClaPresentation(accCla);
    }

    @GetMapping("/fullPath/{id}")
    public String getFullPath(@PathVariable("id") String id){

        return service.showFullPath(service.getAccCla(id));
    }
}