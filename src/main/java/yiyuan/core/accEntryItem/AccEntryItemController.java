package yiyuan.core.accEntryItem;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "AccEntryItem 接口测试")
@RestController
@RequestMapping("/AccEntryItem")
public class AccEntryItemController {
    @Autowired
    AccEntryItemService service;

    @PostMapping("/add")
    public AccEntryItem add(@RequestBody AccEntryItem accEntryItem) {

        return service.addAccEntryItem(accEntryItem);
    }

    @GetMapping("/get/{id}")
    public AccEntryItem getAccEntryItem(@PathVariable(value = "id") String id) {
        return service.getAccEntryItem(id);
    }

    @PutMapping("/update")
    public AccEntryItem update(@RequestBody AccEntryItem accEntryItem) {
        return service.updateAccEntryItem(accEntryItem);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) {

        service.deleteAccEntryItem(id);
        return "delete accEntryItem by id :" + id;
    }

    @GetMapping("/findAll")
    public List<AccEntryItem> findAll(){
        return service.findAll();
    }
}