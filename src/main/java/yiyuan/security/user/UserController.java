package yiyuan.security.user;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Api(tags = "User 接口测试")
@RestController
@RequestMapping("/User")
public class UserController {
@Autowired
UserService service;

    @PostMapping("/add")
    public User add(@RequestBody User user){

         return service.addUser(user);
    }
    @GetMapping("/get/{id}")
    public User getUser(@PathVariable(value = "id")String id){
      return service.getUser(id);
    }
   @PutMapping("/update")
    public User update(@RequestBody User user){
     return service.updateUser(user);
    }
    @DeleteMapping("/delete/{id}")
   public String delete(@PathVariable("id") String id){

      service.deleteUser(id);
      return "delete user by id :" +id;
    }
}