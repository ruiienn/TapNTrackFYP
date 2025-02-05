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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Controller
public class RewardsController {

	@Autowired
	private RewardsRepository rewardsRepository;

	@Autowired
	private MemberRewardsRepository memberRewardsRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	MemberDetailsService memberDetailsService;

	@Autowired
	private RewardsService rewardsService;

	@Autowired
	private MemberRewardsService memberRewardsService;

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private PointsRewardedService pointsRewardedService;


	@GetMapping("/rewards")
	public String viewRewards(@RequestParam(value = "filter", required = false) String filter, Model model,
			Principal principal) {

		List<Rewards> rewards;

		if ("asc".equals(filter)) {
			rewards = rewardsService.getRewardsSortedByPointsAsc();
		} else if ("desc".equals(filter)) {
			rewards = rewardsService.getRewardsSortedByPointsDesc();
		} else {
			rewards = rewardsService.getAllRewards();
		}

		model.addAttribute("listRewards", rewards);

		if (principal != null) {
			String username = principal.getName();
			Member member = memberRepository.findByUsername(username);
			if (member != null) {
				int redeemedToday = memberRewardsRepository.countByMemberAndRedeemedDate(member, LocalDate.now());
				model.addAttribute("redeemedToday", redeemedToday); // Pass this to the view
			}
		}

		// The same logic repeated to ensure no removal from either method
		List<Rewards> rewardsSecond;

		if ("asc".equals(filter)) {
			rewardsSecond = rewardsService.getRewardsSortedByPointsAsc();
		} else if ("desc".equals(filter)) {
			rewardsSecond = rewardsService.getRewardsSortedByPointsDesc();
		} else {
			rewardsSecond = rewardsService.getAllRewards();
		}

		model.addAttribute("listRewardsSecond", rewardsSecond);

		if (principal != null) {
			String username = principal.getName();
			Member member = memberRepository.findByUsername(username);
			if (member != null) {
				int redeemedTodaySecond = memberRewardsRepository.countByMemberAndRedeemedDate(member, LocalDate.now());
				model.addAttribute("redeemedTodaySecond", redeemedTodaySecond); // Pass this to the view
			}
		}

		return "view_rewards";
	}

	@GetMapping("/rewards/add")
	public String addRewards(Model model) {
		model.addAttribute("rewards", new Rewards());
		return "add_rewards";
	}

	@GetMapping("/rewards/edit/{rewardsId}")
	public String editRewards(@PathVariable("rewardsId") Integer rewardsId, Model model) {
		Rewards rewards = rewardsRepository.findById(rewardsId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid reward ID:" + rewardsId));
		model.addAttribute("rewards", rewards);
		return "edit_rewards";
	}

	@GetMapping("/rewards/view/{rewardsId}")
	public String viewSingleReward(@PathVariable("rewardsId") Integer rewardsId, Model model) {
		Rewards rewards = rewardsRepository.findById(rewardsId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid reward ID: " + rewardsId));
		model.addAttribute("rewards", rewards);
		return "view_single_reward";
	}

	@GetMapping("/redeem")
	public String showRedeemPage(Model model, Principal principal) {
	    // Ensure the user is authenticated
	    String username = principal.getName();
	    Member member = memberRepository.findByUsername(username);

	    // Handle the case where the member does not exist
	    if (member == null) {
	        model.addAttribute("error", "Member not found.");
	        return "error"; // Redirect to an error page
	    }

	    // Calculate total points and add it to the model
	    Integer totalPoints = pointsRewardedService.calculateTotalPoints(member);
	    model.addAttribute("memberPoints", totalPoints != null ? totalPoints : 0);

	    // Return the redeem page
	    return "redeem";
	}


//	@GetMapping("/redeem")
//	public String redeem(Model model, Principal principal) {
//		// Fetch all rewards
//		List<Rewards> listRewards = rewardsRepository.findAll();
//		model.addAttribute("listRewards", listRewards);
//
//		// Check if a user is authenticated (principal is not null)
//		if (principal != null) {
//			String username = principal.getName();
//			Member member = memberRepository.findByUsername(username);
//
//			// If member exists, fetch their points
//			if (member != null) {
//				model.addAttribute("memberPoints", member.getPoints());
//			} else {
//				model.addAttribute("memberPoints", 0); // Default points for invalid member
//				model.addAttribute("error", "Member not found.");
//			}
//		} else {
//			// If the user is not authenticated, show generic guest points
//			Member guestMember = memberRepository.findByUsername("guest");
//
//			if (guestMember != null) {
//				model.addAttribute("memberPoints", guestMember.getPoints());
//			} else {
//				model.addAttribute("memberPoints", 0); // Default points if guest member doesn't exist
//			}
//		}
//
//		return "redeem"; // Return the 'redeem' view
//	}

	@PostMapping("/rewards/save")
	public String saveRewards(@Valid Rewards rewards, BindingResult result,
			@RequestParam("rewardsImage") MultipartFile imgFile, Model model) {

		if (rewardsService.existByDescription(rewards.getDescription())) {
			model.addAttribute("errorMessage", "This reward already exists.");
			return "add_rewards";
		}

		if (result.hasErrors()) {
			return "add_rewards";
		}

		if (rewards.getStatus() == null || rewards.getStatus().isEmpty()) {
			rewards.setStatus("Available");
		}

		String imageName = imgFile.getOriginalFilename();
		rewards.setImgName(imageName);
		Rewards savedRewards = rewardsRepository.save(rewards);

		try {
			String uploadDir = "uploads/rewards/" + savedRewards.getRewardsId();
			Path uploadPath = Paths.get(uploadDir);
			System.out.println("Directory path: " + uploadPath);
			if (!Files.exists(uploadPath)) {

				Files.createDirectories(uploadPath);

			}
			Path fileToCreatePath = uploadPath.resolve(imageName);
			System.out.println("File path: " + fileToCreatePath);
			Files.copy(imgFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException io) {
			io.printStackTrace();
		}

		return "redirect:/rewards";
	}

	@PostMapping("rewards/delete/{rewardsId}")
	public String saveDeletedRewards(@PathVariable("rewardsId") Integer rewardsId) {
		rewardsRepository.deleteById(rewardsId);
		return "redirect:/rewards";
	}

	@PostMapping("/rewards/edit/{rewardsId}")
	public String savedUpdatedRewards(@PathVariable("rewardsId") Integer rewardsId, @Valid Rewards rewards,
			BindingResult result, @RequestParam("rewardsImage") MultipartFile imgFile) {
		if (result.hasErrors()) {
			return "edit_rewards";
		}

		Rewards existingReward = rewardsRepository.findById(rewardsId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid reward ID:" + rewardsId));

		existingReward.setDescription(rewards.getDescription());
		existingReward.setQuantity(rewards.getQuantity());
		existingReward.setPointsRequired(rewards.getPointsRequired());

		existingReward.setStatus(rewards.getStatus());

		if (!imgFile.isEmpty()) {
			String imageName = imgFile.getOriginalFilename();
			existingReward.setImgName(imageName);

			try {
				String uploadDir = "uploads/rewards/" + rewardsId;
				Path uploadPath = Paths.get(uploadDir);
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				Path fileToCreatePath = uploadPath.resolve(imageName);
				Files.copy(imgFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException io) {
				io.printStackTrace();
			}
		}

		rewardsRepository.save(existingReward);
		return "redirect:/rewards";

	}

	@GetMapping("/redeem/process/{rewardsId}")
	public String processRedemption(@PathVariable int rewardsId, Model model, Principal principal) {
		String username = principal.getName();

		Member member = memberRepository.findByUsername(username);
		if (member == null) {
			model.addAttribute("error", "Member not found.");
			return "redirect:/rewards";
		}

		Rewards rewards = rewardsRepository.findById(rewardsId);
		if (rewards == null) {
			model.addAttribute("error", "Reward not found.");
			return "redirect:/rewards";
		}

		// Logic from the first method: Check reward availability and daily limit
		if (rewards.getQuantity() <= 0 || !"Available".equals(rewards.getStatus())) {
			model.addAttribute("error", "This reward is unavailable or out of stock.");
			return "redirect:/rewards";
		}

		int redeemedToday = memberRewardsRepository.countByMemberAndRedeemedDate(member, LocalDate.now());
		if (redeemedToday >= 3) {
			model.addAttribute("error", "You have reached the maximum redemption limit of 3 rewards per day.");
			return "redirect:/rewards";
		}

		// Logic from the second method: Check points and process redemption
		if (member.getPoints() >= rewards.getPointsRequired() && rewards.getQuantity() > 0) {
			member.setPoints(member.getPoints() - rewards.getPointsRequired());
			rewards.setQuantity(rewards.getQuantity() - 1);

			MemberRewards memberRewards = new MemberRewards();
			memberRewards.setMember(member);
			memberRewards.setRewards(rewards);
			memberRewards.setMemberPoints(rewards.getPointsRequired());
			memberRewards.setRedeemedQty(1);
			memberRewards.setRedeemedDate(LocalDate.now());

			memberRewardsService.save(memberRewards);
			memberDetailsService.save(member);
			rewardsRepository.save(rewards);

			return "redirect:/redeem";
		} else {
			model.addAttribute("error", "Insufficient points or reward out of stock.");
			return "redirect:/rewards";
		}
	}
}
