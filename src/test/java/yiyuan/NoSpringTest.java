package yiyuan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
	
	// @Test
	public void test1() {

		Map<String, List<Integer>> map = new HashMap<>();
		String name1 = "pzr";
		List<Integer> list1 = new ArrayList<>();

		list1.add(50);
		list1.add(100);
		map.put(name1, list1);

		List<Integer> list = new ArrayList<>();
		list = map.get(name1);
		list.set(0, 100);

		List<Integer> list2 = new ArrayList<>();
		list2.add(456);
		map.put(name1, list2);

		pt(map.get(name1));
		pt(list);
	}



	private void pt(Object o) {

		System.out.println(o.toString());
	}
}
