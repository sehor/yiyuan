package yiyuan.security.role;

import java.util.List;

import org.springframework.security.access.ConfigAttribute;

public interface RoleService {
	Role addRole(Role role);

	Role getRole(String id);

	Role updateRole(Role role);

	void deleteRole(Role role);

	void deleteRole(String id);

	List<Role> findRolesByUrl(String urlString);
}