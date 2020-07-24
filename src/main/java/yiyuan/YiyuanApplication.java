package yiyuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
//@ComponentScan(basePackages = {"yiyuan","jinDieEntryXLS"})
public class YiyuanApplication {

	public static void main(String[] args) {
		SpringApplication.run(YiyuanApplication.class, args);
	}

}
