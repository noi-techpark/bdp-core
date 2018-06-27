/**
 * reader - Data Reader for the Big Data Platform, that queries the database for web-services
 * Copyright © 2018 OpenDataHub (info@opendatahub.bz.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
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
