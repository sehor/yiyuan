package yiyuan.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User {

	private String name;
	private int age;
	private List<String> favorites=new ArrayList<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	
	public void addFavorite(String favorite) {
		
		 this.favorites.add(favorite);
	}
	
	
	public List<String> getFavorites(){
		return this.favorites;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "name: "+getName()+"\n\r"
		+"age: "+getAge()+"\n\r"
		+"favorites: "+getFavorites().toString()+"\n\r";
	}
	
	
	
}
