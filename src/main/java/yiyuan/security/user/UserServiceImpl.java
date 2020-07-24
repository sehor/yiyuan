package yiyuan.security.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService,UserDetailsService {
	@Autowired
	UserRepository repository;

	@Override
	public User addUser(User user) {
		if(user.getPassword().length()<15) { //没加密
			user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
			}
		return repository.save(user);
	}

	@Override
	public User getUser(String id) {
		return repository.findById(id).get();
	}

	@Override
	public User updateUser(User user) {
		if(user.getPassword().length()<15) { //没加密
			user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
			}
		return repository.save(user);
	}

	@Override
	public void deleteUser(User user) {
		repository.delete(user);
	}

	@Override
	public void deleteUser(String id) {
		repository.deleteById(id);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		UserDetails user=repository.findByUsername(username);
		if(user==null) throw new UsernameNotFoundException("can not find the user named: "+username);
		return user;
	}

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}
	

	
}