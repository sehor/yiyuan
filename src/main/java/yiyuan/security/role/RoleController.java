package yiyuan.security.role;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Api(tags = "Role 接口测试")
@RestController
@RequestMapping("/Role")
public class RoleController {
@Autowired
RoleService service;

    @PostMapping("/add")
    public Role add(@RequestBody Role role){

         return service.addRole(role);
    }
    @GetMapping("/get/{id}")
    public Role getRole(@PathVariable(value = "id")String id){
      return service.getRole(id);
    }
   @PutMapping("/update")
    public Role update(@RequestBody Role role){
     return service.updateRole(role);
    }
    @DeleteMapping("/delete/{id}")
   public String delete(@PathVariable("id") String id){

      service.deleteRole(id);
      return "delete role by id :" +id;
    }
}