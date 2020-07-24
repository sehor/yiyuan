package yiyuan.security.user;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
	User addUser(User user);

	User getUser(String id);

	User updateUser(User user);

	void deleteUser(User user);

	void deleteUser(String id);
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
	
	List<User> findAll();
}