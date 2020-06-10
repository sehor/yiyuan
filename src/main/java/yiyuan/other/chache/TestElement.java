package yiyuan.other.chache;

public class TestElement {

	private int id;
	private String name;
	
	public TestElement(int id,String name) {
		this.id=id;
		this.name=name;
	}
	
	public TestElement(String name) {
		this.id=0;
		this.name=name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
