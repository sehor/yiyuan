package yiyuan.security.menu;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Api(tags = "Menu 接口测试")
@RestController
@RequestMapping("/Menu")
public class MenuController {
@Autowired
MenuService service;

    @PostMapping("/add")
    public Menu add(@RequestBody Menu menu){

         return service.addMenu(menu);
    }
    @GetMapping("/get/{id}")
    public Menu getMenu(@PathVariable(value = "id")String id){
      return service.getMenu(id);
    }
   @PutMapping("/update")
    public Menu update(@RequestBody Menu menu){
     return service.updateMenu(menu);
    }
    @DeleteMapping("/delete/{id}")
   public String delete(@PathVariable("id") String id){

      service.deleteMenu(id);
      return "delete menu by id :" +id;
    }
}