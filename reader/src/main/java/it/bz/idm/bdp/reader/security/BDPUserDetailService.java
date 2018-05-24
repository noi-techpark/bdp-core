package it.bz.idm.bdp.reader.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.authentication.BDPUser;
import it.bz.idm.bdp.dal.util.JPAUtil;

@Service
public class BDPUserDetailService implements UserDetailsService{

	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		EntityManager manager = JPAUtil.createEntityManager();
		BDPUser u = BDPUser.findByEmail(manager, username);
		if (u== null)
				throw new UsernameNotFoundException("Could not find user");
		UserBuilder builder = User.withUsername(username);
		builder.roles(castRoles(u.getRoles()));
		builder.password(u.getPassword());
		manager.close();
		return builder.build();
	}

	private String[] castRoles(Collection<BDPRole> roles) {
		List<String> s = new ArrayList<>(); 
		for (Iterator<BDPRole> iterator = roles.iterator();iterator.hasNext();) {
			BDPRole next = iterator.next();
			s.add(next.getName());
		}
		return s.toArray(new String[roles.size()]);
	}


}
