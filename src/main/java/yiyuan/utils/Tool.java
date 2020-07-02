package yiyuan.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import yiyuan.JinDie.Origin.Origin;
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
	
	
	public static List<Origin> cloneOrigins(List<Origin> list){
		List<Origin> cList = new ArrayList<>();
		list.forEach(e->cList.add(e.clone()));
		return cList;
	}
	
	public static String getCurrentCompanyName() {
		User user=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		return user.getUsername();
	}
}
