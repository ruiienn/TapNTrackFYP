package fyp.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

	        // Fetch activities from the database
	        List<Activities> activitiesList = activitiesRepository.findAll();

	        // Extract booths dynamically from the activities
	        List<String> boothsList = activitiesList.stream()
	                .map(Activities::getBooth)
	                .distinct()
	                .collect(Collectors.toList());

	        PointsRewarded pointsRewarded = new PointsRewarded();
	        pointsRewarded.setMember(member);

	        model.addAttribute("pointsRewarded", pointsRewarded);
	        model.addAttribute("activitiesList", activitiesList); // Pass the full activities list
	        model.addAttribute("boothsList", boothsList); // Pass the unique list of booths

	        return "pointsRewarded";
	    }
	    

	    @GetMapping("/pointsRewarded/save")
	    public String redirectToPointsHistory() {
	        // Redirect to the points history page
	        return "redirect:/history"; // Replace with the actual mapping for your points history page
	    }
	    
	    @PostMapping("/pointsRewarded/save")
	    public String savePointsRewarded(@ModelAttribute PointsRewarded pointsRewarded, Model model) {
	        // Here, you can process the submitted form data, e.g., save it to the database
	        System.out.println("Activity: " + pointsRewarded.getActivityForm());
	        System.out.println("Booth: " + pointsRewarded.getBoothForm());
	        System.out.println("Points: " + pointsRewarded.getPointsRewardedForm());

	        // Save the PointsRewarded object to the database (if required)
	        // pointsRewardedRepository.save(pointsRewarded);

	        // Redirect to the points history page
	        return "redirect:/pointsRewarded/save"; // Redirect to a confirmation or another relevant page
	    }

	}
