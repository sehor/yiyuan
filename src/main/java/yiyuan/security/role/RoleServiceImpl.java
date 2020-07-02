package yiyuan.security.role;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import yiyuan.security.menu.Menu;
import yiyuan.security.menu.MenuService;

@Service
public class RoleServiceImpl implements RoleService {
	@Autowired
	RoleRepository repository;
	@Autowired
	MenuService menuService;
	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	@Override
	public Role addRole(Role role) {
		return repository.save(role);
	}

	@Override
	public Role getRole(String id) {
		return repository.findById(id).get();
	}

	@Override
	public Role updateRole(Role role) {
		return repository.save(role);
	}

	@Override
	public void deleteRole(Role role) {
		repository.delete(role);
	}

	@Override
	public void deleteRole(String id) {
		repository.deleteById(id);
	}

	@Override
	public List<Role> findRolesByUrl(String urlString) {
		// TODO Auto-generated method stub
		List<Role> roles = repository.findAll().stream().filter(r -> {
			for (Menu menu : r.getMenus()) {
				if (antPathMatcher.match(menu.getPattern(), urlString)) {
					return true;
				}
			}
			return false;

		}).collect(Collectors.toList());

		return roles;
	}
}