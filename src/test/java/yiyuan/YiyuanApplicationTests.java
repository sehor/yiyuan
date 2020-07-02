package yiyuan;

import static org.assertj.core.api.Assertions.entry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hpsf.Array;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;



import yiyuan.JinDie.OriginType;
import yiyuan.JinDie.Classification.Classfication;
import yiyuan.JinDie.Classification.ClassficationRepository;
import yiyuan.JinDie.Classification.ClassficationService;
import yiyuan.JinDie.Origin.Origin;
import yiyuan.JinDie.Origin.OriginService;
import yiyuan.other.FileStructure;
import yiyuan.other.VATCollector;
import yiyuan.security.menu.Menu;
import yiyuan.security.menu.MenuService;
import yiyuan.security.role.Role;
import yiyuan.security.role.RoleService;
import yiyuan.security.user.User;
import yiyuan.security.user.UserService;
import yiyuan.utils.CompanyProperties;
import yiyuan.utils.msofficetools.BeansToXLSTransform;
import yiyuan.utils.msofficetools.DefaultBeansToXLSTransform;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YiyuanApplicationTests {
	@Autowired
	 CompanyProperties companyPro;
	@Autowired 
	MongoOperations template;
	
	@Autowired
	ClassficationRepository classRepository;
	
	@Autowired
	OriginService originService;
	
    @Autowired
    ClassficationService claService;
	//@Test
	public void contextLoads() {
	}
	
	//@Test
	public void test1() {
		
	
		/*
		 * User user= template.findOne(Query.query(Criteria.where("name").is("cherry")),
		 * User.class,"user1Colleciton");
		 * 
		 * System.out.print(user!=null?user.toString():"find nothing!");
		 */
		
		List<Classfication> list=classRepository.findByCompanyName("深圳市安欧科技有限公司");
		list.forEach(e->e.setCompanyName("AOKJ"));
		classRepository.saveAll(list);
		  
	}
	
	
	//@Test 
	public void test2() {

		
		 FileStructure fs=new FileStructure("D:\\work\\finace\\创和\\各种申报表");
		 VATCollector visitor=new VATCollector();
		 fs.handle(visitor);
		 List<Origin> origins=new ArrayList<>();
		 visitor.getRecords().forEach(e->{
			if(e.get应交税款()>0.01) {
				System.out.println(e.getDate()+" 应交税款："+e.get应交税款()+"   累计进项："+e.get累计进项税额()+"  累计应交税款："+e.get累计应交税款());
				Origin origin=new Origin();
				LocalDate date=LocalDate.parse(e.getDate()+"30", DateTimeFormatter.ofPattern("yyyyMMdd"));
				origin.setOccur_date(date);
				
				origin.setType(OriginType.Handle_VTA.value);
				origin.setAmout(e.get应交税款());
	            origins.add(origin);
	            
			}
			
	    HSSFWorkbook book= new DefaultBeansToXLSTransform<Origin>(Origin.class).createWorkbook(origins);
	    try(OutputStream out=new FileOutputStream(new File("C:\\Users\\pzr\\Desktop\\应交税金.xls"))) {
	    	
	    	 book.write(out);
	    	 book.close();
	    	
	    } catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	     
		});
	}

	//@Test
	public void test3() {
         String ostr="短信服务费";
		 String[] strs=companyPro.getKeyword().get("bankFee").split("\\|");
		 for(String str:strs) {
			 if(ostr.contains(str)) {
				pt(true);
				return;
			 }
		 }
		 pt(false);
	}
	
	//@Test
	public void cacheTest() {
		
		claService.getByName("银行存款");
		
	}
	
	@Autowired
	UserService userService;
	@Autowired
	RoleService roleService;
	@Autowired
	MenuService menuService;
	@Test
	public void securitTest() {
		//Menu menu=new Menu();
		//menu.setPattern("*/admin");
		//Menu sMenu=menuService.addMenu(menu);
		
		/*
		 * Role role=new Role(); role.setName("Admin"); role.setNameZh("管理员");
		 * role.setMenus(List.of(sMenu));
		 * 
		 * Role sRole=roleService.addRole(role);
		 * 
		 * 
		 * User user=new User();
		 * 
		 * user.setUsername("kitty"); user.setPassword("kitty");
		 * user.setEmail("kitty'sEmail@123.com"); user.setPhone("kitty12345");
		 * user.setRoles(List.of(sRole)); userService.addUser(user);
		 */
		BCryptPasswordEncoder bc=new BCryptPasswordEncoder();
		User user=(User) userService.loadUserByUsername("kitty");
		user.setPassword(bc.encode("kitty"));
		User sUser=userService.addUser(user);
		pt(sUser);
		
	}
	

	
	private void pt(Object o) {
		System.out.println(o.toString());
	}
}
