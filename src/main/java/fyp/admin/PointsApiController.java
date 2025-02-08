package fyp.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class PointsApiController {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PointsRewardedService pointsRewardedService;

	@GetMapping("/api/member/points")
	public int getMemberPoints(@RequestParam(required = false) Integer memberId, Principal principal) {
		Member member = null;

		// If memberId is provided, fetch member by ID
		if (memberId != null) {
			member = memberRepository.findById(memberId).orElse(null);
		}
		// If no memberId is provided, use Principal to fetch the logged-in user
		else if (principal != null) {
			String username = principal.getName();
			member = memberRepository.findByUsername(username);
		}

		// Calculate and return the total points if member is found
		if (member != null) {
			return pointsRewardedService.calculateTotalPoints(member);
		}

		// Return 0 if no member is found
		return 0;
	}
}