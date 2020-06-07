package yiyuan.core.accountingEntry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/accountingEntry")
public class AccountEntryController {
    @Autowired
    AccountingEntryService service;
    @PostMapping("/add")
    public AccountingEntry addOne(@RequestBody  AccountingEntry accountingEntry){
         return service.add(accountingEntry);
    }

    @GetMapping("/getAll")
    public List<AccountingEntry> getAll(){

        return service.findAll();
    }

}
