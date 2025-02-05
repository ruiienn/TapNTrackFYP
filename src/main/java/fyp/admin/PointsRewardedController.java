package fyp.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PointsRewardedController {

	@Autowired
	private ActivityService activityService; // Inject the service

	@GetMapping("/pointsRewarded")
	public String showPointsRewardedForm(Model model) {
		// Fetch all activities
		List<Activities> activities = activityService.getAllActivities();
		model.addAttribute("activities", activities);
		return "pointsRewardedForm"; // View name
	}
}
