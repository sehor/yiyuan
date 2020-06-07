package yiyuan.utils;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Configuration
@ConfigurationProperties(prefix = "xx")
public class TXMachinesProperties {
	
	 private List<Map<String, String>> machine1s;
	 private String name;
	 
	    public List<Map<String, String>> getMachine1s() {
	        return machine1s;
	    }
	 
	    public void setMachine1s(List<Map<String, String>> machine1s) {
	        this.machine1s = machine1s;
	    }

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	    
	    

}
