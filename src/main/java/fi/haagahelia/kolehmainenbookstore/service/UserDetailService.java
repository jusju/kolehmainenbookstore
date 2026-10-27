package fi.haagahelia.kolehmainenbookstore.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fi.haagahelia.kolehmainenbookstore.domain.AppUser;
import fi.haagahelia.kolehmainenbookstore.domain.AppUserRepository;

@Service
public class UserDetailService implements UserDetailsService  {
	
	private final AppUserRepository repository;
	
	// Constructor Injection
	public UserDetailService(AppUserRepository appUserRepository) {
		this.repository = appUserRepository; 
	}
	
    @Override
    public UserDetails loadUserByUsername(String username) 
		throws UsernameNotFoundException {   
    	
		AppUser curruser = repository.findByUsername(username);
       
		if (curruser == null) {
        	throw new UsernameNotFoundException("User not found: " + username);
    	}

		UserDetails user = new org.springframework.security.core.userdetails.User(curruser.getUsername(), curruser.getPasswordHash(), 
        AuthorityUtils.createAuthorityList(curruser.getRole()));
        return user;
    }   
}