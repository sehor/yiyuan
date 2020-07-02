package yiyuan.security.menu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;


@Table(name = "menu")
@ApiModel(value = "", description = "")
public class Menu {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "")
	private String id;

	@Column()
	@ApiModelProperty(value = "")
	private String pattern;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getPattern() {
		return this.pattern;
	}
	
	
	public String toString() {
		
		return "id:"+id+"  pattern:"+pattern;
	}
}