package yiyuan;

public class Person {

	String name;
	int age;
	
	public Person(String name,int age) {
		// TODO Auto-generated constructor stub
		this.age=age;
		this.name=name;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + "]";
	}
	
	
}
