/**
 * 
 * I declare that this code was written by me, xandr. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: xandra
 * Student ID: 22022591
 * Date created: 2024-Oct-28 2:40:24 pm 
 * 
 */
package fyp.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



/**
 * @author xandr
 *
 */

public class MemberDetailsService implements UserDetailsService {

	 @Autowired 
	   private MemberRepository memberRepository; 
	 
	   @Override 
	   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { 
	     Member member = memberRepository.findByUsername(username); 
	     if (member == null) { 
	       throw new UsernameNotFoundException("Could not find user"); 
	     } 
	     return new MemberDetails(member); 
	   } 
	    public Member save(Member member) {
	        return memberRepository.save(member);
	    }
	    
	    public boolean usernameExists(String username) {
	        return memberRepository.existsByUsername(username);
	    }
	    
	    public void saveMember(Member member) {
	        // Encrypt password before saving it
	        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	        String encodedPassword = encoder.encode(member.getPassword());
	        member.setPassword(encodedPassword);

	        // Save member to the database
	        memberRepository.save(member);
	    }

	}