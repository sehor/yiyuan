package yiyuan.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.core.context.SecurityContextHolder;

import jinDieEntryXLS.beans.RawInfo;
import yiyuan.security.user.User;

public class Tool {

	//t and it's fields must can be serializable 
	@SuppressWarnings("unchecked")
	public static <T> T clone(T t) {
		T o=null;
		
		try {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();	
		ObjectOutputStream oos=new ObjectOutputStream(baos);
		oos.writeObject(t);
		
		ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
		o=(T) ois.readObject();	
		}catch(IOException e) {
			e.printStackTrace();
		}catch(ClassNotFoundException e) {
			
			e.printStackTrace();
		}
			
		return o;	
	}
	
	
	public static <T> List<T> clone(List<T> list){
		List<T> cList = new ArrayList<>();
		list.forEach(e->cList.add(clone(e)));
		return cList;
	}
	
	
	public static List<RawInfo> cloneOrigins(List<RawInfo> list){
		List<RawInfo> cList = new ArrayList<>();
		list.forEach(e->cList.add(e.clone()));
		return cList;
	}
	
	public static String getCurrentCompanyName() {
		User user=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		return user.getUsername();
	}
	
	
	public static List<String> getMatchers(String reg,String search){
		List<String> strs=new ArrayList<>();
		Pattern pattern=Pattern.compile(reg);
		Matcher matcher=pattern.matcher(search);
		
	   //注意 返回的是group1；
		while(matcher.find()) {
			strs.add(matcher.group(1));
		}
			
		return strs;
		
	}
	
	public static String getMatcher(String reg,String search) {
		
		List<String> strs=getMatchers(reg, search);
		return strs.size()>0?strs.get(0):"";
	}
	
}
