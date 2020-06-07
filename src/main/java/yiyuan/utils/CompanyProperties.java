package yiyuan.utils;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
//@PropertySource(value  = "classpath:test.yml",encoding = "UTF-8")
@ConfigurationProperties(prefix = "props")
public class CompanyProperties {

	private Map<String, Map<String,String>> companies;
	private Map<String,String> keyword;

	public Map<String, Map<String, String>> getCompanies() {
		return companies;
	}

	public void setCompanies(Map<String, Map<String, String>> companies) {
		this.companies = companies;
	}

	public Map<String, String> getKeyword() {
		return keyword;
	}

	public void setKeyword(Map<String, String> keyword) {
		this.keyword = keyword;
	}

	

	
  
}
