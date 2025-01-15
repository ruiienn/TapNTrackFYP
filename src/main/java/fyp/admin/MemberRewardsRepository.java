/**
 * 
 * I declare that this code was written by me, ${user}. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: 
 * Student ID: 
 * Date created: ${id:date('YYYY-MMM-dd')} ${time} 
 * 
 */

package fyp.admin;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRewardsRepository extends JpaRepository<MemberRewards, Integer>{

	public List<MemberRewards> findByMember(Member member);
	
	public List<MemberRewards> findByRewards(Rewards rewards);
}
