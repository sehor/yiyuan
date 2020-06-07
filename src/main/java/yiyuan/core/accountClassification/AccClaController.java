package yiyuan.core.accountClassification;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import javassist.NotFoundException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @PutMapping("/update/{id}")
    void updateById(@RequestBody AccCla accCla,@PathVariable("id") String id){
         service.updateByid(accCla,id);
    }
    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) throws  NotFoundException {

        service.deleteAccCla(id);
        return "delete accCla by id :" + id;
    }

    @GetMapping("/rootPresentation/{type}")
    public AccClaPresentation getRootPresentation(@PathVariable("type") String type) {
        AccCla accCla = new AccCla();
        accCla.setId("root");
        accCla.setName("RootNode");

        return service.createAccClaPresentation(accCla,type);
    }

    @GetMapping("/fullPath/{id}")
    public String getFullPath(@PathVariable("id") String id) {

        return service.showFullPath(service.getAccCla(id));
    }


    @GetMapping("/readFromXls")
    public List<AccCla> readFromXls() throws IOException {
        FileInputStream inputStream = new FileInputStream(new File("D:\\temp\\会计科目表.xls"));
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        List<AccCla> accClas = new ArrayList<>();
        HSSFSheet sheet = workbook.getSheetAt(5);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            if (row == null) break;
            if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) break;

            AccCla accCla = new AccCla();
            accCla.setParentId("root");
            accCla.setCreateDate(LocalDate.now());
            row.getCell(1).setCellType(CellType.STRING);
            accCla.setId(row.getCell(1).getStringCellValue());
            accCla.setNameZh(row.getCell(2).getStringCellValue());
            accCla.getCategories().put("categoryInBalance", "profitAndLoss");
            accCla.setLevel("supreme");
            if (row.getCell(3).getCellType() != CellType.BLANK) {
                accCla.setForIndustry(row.getCell(3).getStringCellValue());
            } else {
                accCla.setForIndustry("normal");
            }
            accClas.add(accCla);

            AccCla accCla1 = new AccCla();
            accCla1.setParentId("root");
            accCla1.setCreateDate(LocalDate.now());
            if (row.getCell(5).getCellType() == CellType.BLANK) break;
            row.getCell(5).setCellType(CellType.STRING);
            accCla1.setId(row.getCell(5).getStringCellValue());
            accCla1.setNameZh(row.getCell(6).getStringCellValue());
            accCla1.getCategories().put("categoryInBalance", "profitAndLoss");
            accCla1.setLevel("supreme");
            if (row.getCell(7).getCellType() != CellType.BLANK) {
                accCla1.setForIndustry(row.getCell(7).getStringCellValue());
            } else {
                accCla1.setForIndustry("normal");
            }
            accClas.add(accCla1);

        }

        inputStream.close();
        workbook.close();
        accClas.forEach(accCla -> service.addAccCla(accCla));

        return accClas;
    }

    @GetMapping("/setName")
    public void setName() throws IOException {
        FileInputStream inputStream = new FileInputStream(new File("D:\\temp\\会计科目表.xls"));
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        HSSFSheet sheet = workbook.getSheetAt(6);
        String reg = "^\\w+$";
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            if (row == null) break;

            if (row.getCell(1).getCellType() != CellType.BLANK) {
                String nameZh = row.getCell(1).getStringCellValue();
                AccCla accCla = service.getAccClaByNameZh(nameZh);
                if (accCla != null) {
                    List<String> strings = new ArrayList<>();
                    for (int j = 2; j <= row.getLastCellNum(); j++) {
                        if (row.getCell(j)==null||row.getCell(j).getCellType() == CellType.BLANK) break;
                        String str = row.getCell(j).getStringCellValue();
                        if (str.matches(reg)) strings.add(str);
                    }
                    if(strings.size()>0) accCla.setName(String.join(" ",strings));
                    service.updateAccCla(accCla);
                }
            }
        }

    }

    @GetMapping("/findAll")
    public List<AccCla> findAll(){
        System.out.println("from.....");
        return service.getAll();
    }

    @GetMapping("/change")
    public void change(){
        service.change();
    }
}