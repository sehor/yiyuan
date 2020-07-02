package yiyuan.security.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import yiyuan.security.menu.Menu;

import java.util.List;

import javax.persistence.*;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
@ApiModel(value = "", description = "")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "")
	private String id;

	@Column()
	@ApiModelProperty(value = "")
	private String name;

	@Column()
	@ApiModelProperty(value = "")
	private String nameZh;

	private List<Menu> menus;
	
	

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setNameZh(String nameZh) {
		this.nameZh = nameZh;
	}

	public String getNameZh() {
		return this.nameZh;
	}
	
	public String toString() {
		return "id: "+id+"  name: "+name+"  nameZh: "+nameZh;
		
	}
}