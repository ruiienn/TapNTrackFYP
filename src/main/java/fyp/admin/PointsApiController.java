package fyp.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class PointsApiController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointsRewardedService pointsRewardedService;

    @GetMapping("/api/member/points")
    public int getMemberPoints(Principal principal) {
        // Get the logged-in user's username
        String username = principal.getName();

        // Fetch the corresponding member
        Member member = memberRepository.findByUsername(username);

        if (member != null) {
            // Calculate and return the total points
            return pointsRewardedService.calculateTotalPoints(member);
        }

        // Return 0 if the member is not found
        return 0;
    }
}
