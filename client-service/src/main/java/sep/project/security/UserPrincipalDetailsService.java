package sep.project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sep.project.model.Seller;
import sep.project.repositories.SellerRepository;

@Service
public class UserPrincipalDetailsService implements UserDetailsService{
	
	@Autowired
	private SellerRepository sellerRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		Seller user = sellerRepository.findByEmailAndDeleted(username, false);
		
		if(user == null) {
			throw new UsernameNotFoundException("User with the username " + username + " doesn't exist.");
		}
		
		return new UserPrincipal(user);
	}
	
	

}
