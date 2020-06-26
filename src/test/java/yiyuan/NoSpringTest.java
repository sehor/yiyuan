package yiyuan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class NoSpringTest {

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

	@Test
	public void test2() {
      Collection<String> set=new HashSet<>();
      set.add("a");
      set.add("b");
      set.add("a");
      
      pt(set.toString());
        
	}

	private void pt(Object o) {

		System.out.println(o.toString());
	}
}
