package fyp.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PointsRewardedController {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ActivitiesRepository activitiesRepository;

	@Autowired
	private PointsRewardedService pointsRewardedService;

	@Autowired
	private PointsRewardedRepository pointsRewardedRepository;

	@GetMapping("/pointsRewarded")
	public String showPointsRewardedForm(Model model, Principal principal) {
		if (principal == null) {
			return "redirect:/login"; // Redirect to login if not authenticated
		}

		String username = principal.getName();
		Member member = memberRepository.findByUsername(username);

		if (member == null) {
			model.addAttribute("error", "Member not found.");
			return "error"; // Redirect to an error page if member is null
		}

		// Fetch activities with booth information
		List<Activities> activitiesList = activitiesRepository.findAll();

		PointsRewarded pointsRewarded = new PointsRewarded();
		pointsRewarded.setMember(member);

		model.addAttribute("pointsRewarded", pointsRewarded);
		model.addAttribute("activitiesList", activitiesList); // Ensure booth info is included

		return "pointsRewarded";
	}

	@GetMapping("/pointsRewarded/save")
	public String redirectToPointsHistory() {
		// Redirect to the points history page
		return "redirect:/history"; // Replace with the actual mapping for your points history page
	}

	@PostMapping("/pointsRewarded/save")
	public String savePointsRewarded(@ModelAttribute PointsRewarded pointsRewarded, Model model, Principal principal) {
		// Ensure the Principal (authenticated user) is available
		if (principal == null) {
			throw new IllegalArgumentException("User not authenticated");
		}

		String username = principal.getName();
		Member member = memberRepository.findByUsername(username);

		if (member == null) {
			throw new IllegalArgumentException("Member not found");
		}

		pointsRewarded.setMember(member);

		// Validate PointsRewarded object fields
		if (pointsRewarded.getActivityForm() == null || pointsRewarded.getBoothForm() == null
				|| pointsRewarded.getPointsRewardedForm() == null) {
			throw new IllegalArgumentException("PointsRewarded object is invalid");
		}

		pointsRewardedService.save(pointsRewarded);

		model.addAttribute("successMessage", "Points rewarded entry saved successfully");
		return "redirect:/history";
	}

	@GetMapping("/history")
	public String showHistoryPage(Model model, Principal principal) {
		String username = principal.getName();
		Member member = memberRepository.findByUsername(username);

		if (member == null) {
			model.addAttribute("error", "Member not found.");
			return "error";
		}

		// Fetch points history
		List<PointsRewarded> pointsHistory = pointsRewardedRepository.findByMember(member);
		model.addAttribute("listPointsRewarded", pointsHistory);

		// Unwrap total points and handle null values
		Integer totalPoints = pointsRewardedService.calculateTotalPoints(member);
		model.addAttribute("memberPoints", totalPoints != null ? totalPoints : 0);

		return "history"; // Return the correct template
	}

}
