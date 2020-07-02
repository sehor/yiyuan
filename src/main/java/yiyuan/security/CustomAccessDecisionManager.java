package yiyuan.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import yiyuan.security.menu.MenuService;

import java.util.Collection;

@Component
public class CustomAccessDecisionManager implements AccessDecisionManager {
	

	@Autowired
	MenuService menuService;

	@Override
	public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection)
			throws AccessDeniedException, InsufficientAuthenticationException {

		for (ConfigAttribute configAttribute : collection) {
			if (configAttribute.getAttribute().equals("ROLE_LOGIN")
					&& authentication instanceof UsernamePasswordAuthenticationToken) {
				//System.out.println("in CustomAccessDecisionManager.decide," + configAttribute.getAttribute());
				return;
			}
			for (GrantedAuthority authority : authentication.getAuthorities()) {

				//System.out.println("in CustomAccessDecisionManager.decide,  " + "user authority : " + authority + " ; "
				//		+ " configAttribute: " + configAttribute.getAttribute());

				if (authority.getAuthority().equals(configAttribute.getAttribute())) {
					return;
				}
			}
		}

		throw new AccessDeniedException("抱歉，你的权限不足以访问该资源！");

	}

	@Override
	public boolean supports(ConfigAttribute configAttribute) {
		return false;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return false;
	}
}
