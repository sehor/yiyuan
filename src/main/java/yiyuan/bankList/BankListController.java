package yiyuan.bankList;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Api(tags = "BankList 接口测试")
@RestController
@RequestMapping("/BankList")
public class BankListController {
    @Autowired
    BankListService service;

    @ApiOperation(value = "example", notes = "this is a example")
    @ApiResponse(message = "访问成功", code = 200)
    @ApiImplicitParam(value = "str", type = "path", required = false, name = "path variable")
    @GetMapping("/example/{str}")
    public String example(@PathVariable(value = "str") String str) {
        return "你好，" + str;
    }

    @PostMapping("/add")
    public BankList add(@RequestBody BankList bankList) {

        return service.addBankList(bankList);
    }

    @GetMapping("/get/{id}")
    public BankList getBankList(@PathVariable(value = "id") Integer id) {
        return service.getBankList(id);
    }

    @PutMapping("/update")
    public BankList update(@RequestBody BankList bankList) {
        return service.updateBankList(bankList);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {

        service.deleteBankList(id);
        return "delete bankList by id :" + id;
    }

    @GetMapping("/fromFile")
    public List<BankList> getBankListFromFile() throws IOException {

         File file=new File("D:\\temp\\宜源私帐明细-按季度.xlsx");
         List<BankList> bankLists=service.getBankListFromXLSFile(file,0,9);
         bankLists.forEach(bl->service.addBankList(bl));
        return bankLists;
    }
}