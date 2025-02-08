package fyp.admin;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class MemberController {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberDetailsService memberDetailsService;

	// View All Members (Sorted by Points)
	@GetMapping("/members")
	public String viewMembers(Model model) {
		List<Member> listMembers = memberRepository.findAll();
		listMembers.sort((m1, m2) -> Integer.compare(m2.getPoints(), m1.getPoints()));
		model.addAttribute("listMembers", listMembers);
		return "view_member";
	}

	// Show Form to Add Member
	@GetMapping("/members/add")
	public String showAddMemberForm(Model model) {
		if (!model.containsAttribute("message")) {
			model.addAttribute("message", null);
		}
		if (!model.containsAttribute("uniqueLink")) {
			model.addAttribute("uniqueLink", null);
		}
		model.addAttribute("member", new Member());
		return "add_member";
	}

	// Save a New Member and Generate Unique Link
	@PostMapping("/members/save")
	public String saveMember(@Valid @ModelAttribute("member") Member member, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("error", "Validation failed. Please check the form fields.");
			return "add_member";
		}

		// Check if username already exists
		if (memberDetailsService.usernameExists(member.getUsername())) {
			model.addAttribute("errorMessage", "Username already taken. Please choose another one.");
			return "add_member";
		}

		// Encrypt the password
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		member.setPassword(passwordEncoder.encode(member.getPassword()));

		// Assign default role if not provided
		if (member.getRole() == null
				|| (!member.getRole().equals("ROLE_ADMIN") && !member.getRole().equals("ROLE_USER"))) {
			member.setRole("ROLE_USER");
		}

		// Save Member
		Member savedMember = memberRepository.save(member);

		// Generate Unique Link with Correct Member ID
//		String uniqueLink = "https://tapntrackfyp.onrender.com/members/" + savedMember.getId() + "/addPoints";
		String uniqueLink = "https://tapntrackfyp.onrender.com/members/" + savedMember.getId();

		model.addAttribute("uniqueLink", uniqueLink);
		model.addAttribute("success", "Member successfully added!");
		return "add_member";
	}

	// Edit a Member
	@GetMapping("/members/edit/{id}")
	public String editMember(@PathVariable("id") Integer id, Model model) {
		Member member = memberRepository.getReferenceById(id);
		model.addAttribute("member", member);
		return "edit_member";
	}

	@PostMapping("/members/edit/{id}")
	public String saveUpdatedMember(@PathVariable("id") Integer id, Member member) {
		member.setPassword(memberRepository.getReferenceById(id).getPassword());
		memberRepository.save(member);
		return "redirect:/";
	}

	// Delete a Member
	@GetMapping("/members/delete/{id}")
	public String deleteMember(@PathVariable("id") Integer id) {
		memberRepository.deleteById(id);
		return "redirect:/members";
	}

	// View Single Member Profile
	@GetMapping("/members/{id}")
	public String viewSingleMember(@PathVariable("id") Integer id, Model model) {
		Member member = memberRepository.getReferenceById(id);
		model.addAttribute("member", member);
		return "view_single_member";
	}

	// Profile Handling for Logged-in Users
	@GetMapping("/profile")
	public String viewProfile(Model model, Principal principal) {
		String username = principal.getName();
		Member member = memberRepository.findByUsername(username);
		model.addAttribute("member", member);
		return "profile";
	}

	@GetMapping("/profile/edit")
	public String editProfile(Model model, Principal principal) {
		String username = principal.getName();
		Member member = memberRepository.findByUsername(username);
		model.addAttribute("member", member);
		return "edit_profile";
	}

	@PostMapping("/profile/edit")
	public String saveProfile(@ModelAttribute("member") Member member, Principal principal,
			RedirectAttributes redirectAttributes) {
		String username = principal.getName();
		Member existingMember = memberRepository.findByUsername(username);

		if (existingMember == null) {
			redirectAttributes.addFlashAttribute("error", "Member not found.");
			return "redirect:/profile";
		}

		member.setPassword(existingMember.getPassword());
		member.setId(existingMember.getId());
		memberRepository.save(member);
		redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
		return "redirect:/profile";
	}

//	// Admin-Only Access to Points Reward Form
//	@PreAuthorize("hasRole('ROLE_ADMIN')")
//	@GetMapping("/pointsrewarded/form/{id}")
//	public String showPointsRewardForm(@PathVariable("id") Integer id, Model model) {
//		Member member = memberRepository.findById(id).orElseThrow(() -> new RuntimeException("Member not found"));
//
//		model.addAttribute("member", member);
//		return "pointsRewarded";
//	}
//
//	// Process Points Reward (Ensure Correct Member)
//	@PreAuthorize("hasRole('ROLE_ADMIN')")
//	@PostMapping("/pointsrewarded/form/{id}")
//	public String addPoints(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes,
//			Principal principal) {
//		try {
//			memberService.addPoints(id, 10);
//			redirectAttributes.addFlashAttribute("success", "10 points added successfully!");
//		} catch (RuntimeException e) {
//			redirectAttributes.addFlashAttribute("error", "Failed to add points: " + e.getMessage());
//		}
//		return "redirect:/members";
//	}

//	// Prevent Regular Users from Accessing This Page
//	@PreAuthorize("hasRole('ROLE_ADMIN')")
//	@GetMapping("/pointsrewarded/{id}")
//	public String rewardPointsPage(@PathVariable("id") Integer id, Model model) {
//		Member member = memberRepository.findById(id).orElseThrow(() -> new RuntimeException("Member not found"));
//		model.addAttribute("member", member);
//		return "pointsRewarded";
//	}
	
	@PostMapping("/members/{id}/addPoints")
	public String addPointsAndRedirect(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		try {
			memberService.addPoints(id, 10); // Add 10 points to the user
			redirectAttributes.addFlashAttribute("success", "Points added successfully!");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("error", "Failed to add points: " + e.getMessage());
		}
		return "redirect:/members"; // Redirect to the leaderboard
	}
	
	@GetMapping("/members/{id}/addPoints")
	public String addPointsViaGet(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		try {
			memberService.addPoints(id, 10); // Add 10 points to the user
			redirectAttributes.addFlashAttribute("success", "Points added successfully!");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("error", "Failed to add points: " + e.getMessage());
		}
		return "redirect:/members"; // Redirect to the leaderboard
	}
}
