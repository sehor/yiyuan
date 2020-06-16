package yiyuan.JinDie.Classification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Table(name = "classfication")
@ApiModel(value = "", description = "")
public class Classfication {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "")
	private String id;

	@Column()
	@ApiModelProperty(value = "")
	private String 编码;

	@Column()
	@ApiModelProperty(value = "")
	private String 名称;

	@Column()
	@ApiModelProperty(value = "")
	private String 类别;

	@Column()
	@ApiModelProperty(value = "")
	private String 余额方向;
	
	private String companyName;
	
	private String mutilName;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void set编码(String 编码) {
		this.编码 = 编码;
	}

	public String get编码() {
		return this.编码;
	}

	public void set名称(String 名称) {
		this.名称 = 名称;
	}

	public String get名称() {
		return this.名称;
	}

	public void set类别(String 类别) {
		this.类别 = 类别;
	}

	public String get类别() {
		return this.类别;
	}

	public void set余额方向(String 余额方向) {
		this.余额方向 = 余额方向;
	}

	public String get余额方向() {
		return this.余额方向;
	}

	public String getMutilName() {
		return mutilName;
	}

	public void setMutilName(String mutilName) {
		this.mutilName = mutilName;
	}
	
	
}