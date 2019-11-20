package yiyuan.domain;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sale_record")
public class SaleRecord {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String 公司名称;
	private Double 含税金额;
	private String 品名;
	private String 规格;
	private String 单位;
	private Double 单价;
	private Integer 数量;
	private LocalDate date;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Double get含税金额() {
		return 含税金额;
	}

	public void set含税金额(Double 含税金额) {
		this.含税金额 = 含税金额;
	}

	public String get品名() {
		return 品名;
	}

	public void set品名(String 品名) {
		this.品名 = 品名;
	}

	public String get规格() {
		return 规格;
	}

	public void set规格(String 规格) {
		this.规格 = 规格;
	}

	public String get单位() {
		return 单位;
	}

	public void set单位(String 单位) {
		this.单位 = 单位;
	}

	public Double get单价() {
		return 单价;
	}

	public void set单价(Double 单价) {
		this.单价 = 单价;
	}

	public Integer get数量() {
		return 数量;
	}

	public void set数量(Integer 数量) {
		this.数量 = 数量;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String get公司名称() {
		return 公司名称;
	}

	public void set公司名称(String 公司名称) {
		this.公司名称 = 公司名称;
	}


}
