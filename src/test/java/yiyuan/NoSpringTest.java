package yiyuan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Test;

import yiyuan.JinDie.Origin.Origin;
import yiyuan.utils.Tool;

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

		String s="2016年";
		Pattern pattern=Pattern.compile("(\\d+)(年)");
		Matcher matcher=pattern.matcher(s);
		if(matcher.matches()) {
			pt(matcher.group(1));
		}
		
	}

	private void pt(Object o) {

		System.out.println(o.toString());
	}
}
