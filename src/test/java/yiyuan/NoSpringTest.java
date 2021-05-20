package yiyuan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.lang.annotation.Before;
import org.junit.Test;

import yiyuan.JinDie.Origin.Origin;

public class NoSpringTest {

	private Person person1;
	private Person person2;
	private Person person3;
	
	@org.junit.Before
	public void beforetest() {
		
		  person1=new Person("tom", 13);
		  person2=new Person("gerry",14);
		  person3=new Person("bob",15);
	}
	
	 @Test
	public void test1() throws IOException {

		StringBuilder builder=new StringBuilder();
		try(FileReader reader=new FileReader("D:\\work\\finace\\yy\\2019费用数据录入.txt",StandardCharsets.UTF_8)){
			
			char[] buffer=new char[1000];
			while(reader.read(buffer)!=-1) {
				builder.append(buffer);
			}
			pt(builder);
		}
		
		
	String[] lines=	builder.toString().split(System.lineSeparator());
	for(int i=0;i<lines.length;i++) {
		if(lines[i].trim().length()<1) continue;
		pt(getMatchers("(\\d+|\\d+\\.\\d+)元", lines[i]));
		pt(getMatchers("摘要([\u4E00-\u9FA5\\w]*)",  lines[i]));
		pt(getMatchers("日期(\\d+)",  lines[i]));
		pt(getMatchers("科目([\u4E00-\u9FA5\\w\\-]*)",  lines[i]));
		pt("----------------");
		pt("----------------");
	}
	
	 
	}



	private void pt(Object o) {

		System.out.println(o.toString());
	}
	
	private List<String> getMatchers(String reg,String search){
		List<String> strs=new ArrayList<>();
		Pattern pattern=Pattern.compile(reg);
		Matcher matcher=pattern.matcher(search);
		while(matcher.find()) {
			//strs.add(search.substring(matcher.start(),matcher.end()).replaceAll(replaceReg, ""));
			strs.add(matcher.group(1));
		}
		
		return strs;
		
	}
	

	
}
