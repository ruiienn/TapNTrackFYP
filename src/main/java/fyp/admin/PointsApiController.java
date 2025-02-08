package fyp.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.security.Principal;

@RestController
public class PointsApiController {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PointsRewardedService pointsRewardedService;

	// Endpoint to get member points
	@GetMapping("/api/member/points")
	public Map<String, Object> getMemberPoints(@RequestParam(required = false) Integer memberId, Principal principal) {
	    Member member = null;
	    Map<String, Object> response = new HashMap<>(); // Initialize the response map

	    // Fetch member based on memberId or principal
	    if (memberId != null) {
	        member = memberRepository.findById(memberId).orElse(null);
	    } else if (principal != null) {
	        String username = principal.getName();
	        member = memberRepository.findByUsername(username);
	    }

	    // Calculate and return points if a valid member is found
	    if (member != null) {
	        int totalPoints = pointsRewardedService.calculateTotalPoints(member);
	        List<PointsRewarded> history = pointsRewardedService.getMemberPointsHistory(member);

	        // Add points and history to the response
	        response.put("totalPoints", totalPoints);
	        response.put("history", history);
	        return response;
	    }

	    // Return an empty response if no member is found
	    response.put("totalPoints", 0);
	    response.put("history", new ArrayList<>());
	    return response;
	}
}
