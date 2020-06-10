package yiyuan.other.chache;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TestCache {
	


	private List<TestElement> list = new ArrayList<>();

	@Cacheable(value = "testElement")
	public List<TestElement> all() {
        prt("get all");
		
		return List.copyOf(this.list);
	}

	@CachePut(value = "testElement",key = "#result.id" )
	public TestElement save(TestElement testElement) {

		
		if(testElement.getId()<0) {
			throw new IllegalArgumentException("id must be bigger than 0!  But got one which is "+testElement.getId());
		}
		
		if (testElement.getId() == 0) {
			testElement.setId(maxid() + 1);
			this.list.add(testElement);
			return testElement;
		}

		for (int i = 0; i < this.list.size(); i++) {

			if (this.list.get(i).getId() == testElement.getId()) {
				this.list.set(i, testElement);
				return this.list.get(i);
			}
		}
		this.list.add(testElement);

		prt("save ,id is "+testElement.getId());
		return testElement;
	}

	@Cacheable(value = "testElement")
	public TestElement one(int id) {
		prt("get one by id");
		
		for (TestElement te : this.list) {

			if (te.getId() == id) {
				return te;
			}
		}
		return null;
	}
	
	
	@Cacheable(value = "testElement")
	public TestElement one(String name) {
		prt("get one by name");
		for (TestElement te : this.list) {

			if (te.getName().equals(name)) {
				return te;
			}
		}
		return null;
	}

	@Cacheable(value = "testElement")
	public List<TestElement> findLike(String likeName) {
		
		prt("get all like");
		
		List<TestElement> list_like = new ArrayList<>();
		for (TestElement te : this.list) {
			if (te.getName().contains(likeName)) {
				list_like.add(te);
			}

		}
		return list_like;
	}

	@CacheEvict(value = "testElement")
	public boolean delete(TestElement te) {

		return delete(te.getId());

	}

	@CacheEvict(value = "testElement")
	public boolean delete(int id) {

		TestElement te1 = one(id);
		if (te1 == null)
			return false;
		this.list.remove(te1);
		return true;

	}

	@CacheEvict(value = "testElement",allEntries = true)
	public void clearAll() {
		this.list.clear();
	}

	public int maxid() {
		int max = 0;
		for (TestElement te : this.list) {
			max = te.getId() > max ? te.getId() : max;
		}
		return max;
	}

	public List<TestElement> initData() {

	
		 save(new TestElement("Tom"));
		 save(new TestElement("Bob"));		
		 save(new TestElement("Merry"));
		 save(new TestElement("小刚"));
		 save(new TestElement("大牛"));
		 
		return this.list;
	}
	
	private void prt(Object o) {
		System.out.println(o.toString());
	}
}
