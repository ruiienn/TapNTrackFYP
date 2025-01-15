/**
 * 
 * I declare that this code was written by me, xandr. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: xandra
 * Student ID: 22022591
 * Date created: 2024-Oct-27 10:24:48 pm 
 * 
 */
package fyp.admin;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @author xandr
 *
 */
@Controller
public class MemberController {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberDetailsService memberDetailsService;

	@Autowired
	private MemberService memberService;

	@GetMapping("/members")
	public String viewMembers(Model model) {
		// Fetch all members
		List<Member> listMembers = memberRepository.findAll();

		// Sort the list by points in descending order
		listMembers.sort((m1, m2) -> Integer.compare(m2.getPoints(), m1.getPoints()));

		// Add the sorted list to the model
		model.addAttribute("listMembers", listMembers);

		// Return the Thymeleaf template name
		return "view_member"; // This is your leaderboard page
	}

	@GetMapping("/members/add")
	public String addMember(Model model) {
		if (!model.containsAttribute("message")) {
			model.addAttribute("message", null); // Default to null if no message exists
		}
		if (!model.containsAttribute("uniqueLink")) {
			model.addAttribute("uniqueLink", null); // Default to null
		}
		model.addAttribute("member", new Member());
		return "add_member";
	}

	@PostMapping("/members/save")
	public String saveMember(@Valid Member member, BindingResult bindingResult, Model model) {
		// Check for validation errors
		if (bindingResult.hasErrors()) {
			model.addAttribute("error", "Validation failed. Please check the form fields.");
			return "add_member"; // Return to the add member form for correction
		}

		// Encrypt the password
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		member.setPassword(encodedPassword);

		// Assign default role if not provided
		if (member.getRole() == null
				|| (!member.getRole().equals("ROLE_ADMIN") && !member.getRole().equals("ROLE_USER"))) {
			member.setRole("ROLE_USER");
		}

		// Save the member
		Member savedMember = memberRepository.save(member);

		 // Generate the unique link for adding points
	    String uniqueLink = "https://tapntrackfyp.onrender.com/members/" + savedMember.getId() + "/addPoints";

		// Add the unique link to the model
		model.addAttribute("uniqueLink", uniqueLink);
		model.addAttribute("success", "Member successfully added!");

		return "add_member";
	}

	@GetMapping("/members/edit/{id}")
	public String editMember(@PathVariable("id") Integer id, Model model) {
		Member member = memberRepository.getReferenceById(id);
		model.addAttribute("member", member);
		return "edit_member";
	}

	@PostMapping("/members/edit/{id}")
	public String savedUpdatedMember(@PathVariable("id") Integer id, Member member) {
		member.setPassword(memberRepository.getReferenceById(id).getPassword());
		memberRepository.save(member);
		return "redirect:/";
	}

	@GetMapping("/members/delete/{id}")
	public String deleteMember(@PathVariable("id") Integer id) {
		memberRepository.deleteById(id);
		return "redirect:/members";
	}

	// View single member
	@GetMapping("/members/{id}")
	public String viewSingleMember(@PathVariable("id") Integer id, Model model) {

		Member member = memberRepository.getReferenceById(id);
		model.addAttribute("member", member);

		return "view_single_member";
	}

	@GetMapping("/profile")
	public String viewProfile(Model model, Principal principal) {
		String username = principal.getName(); // Get the current logged-in user's username
		Member member = memberRepository.findByUsername(username);
		model.addAttribute("member", member);
		return "profile"; // Thymeleaf template to view profile
	}

	// Add method to edit the current user's profile
	@GetMapping("/profile/edit")
	public String editProfile(Model model, Principal principal) {
		String username = principal.getName();
		Member member = memberRepository.findByUsername(username);
		model.addAttribute("member", member);
		return "edit_profile"; // Thymeleaf template to edit profile
	}

	// Save edited profile details
	@PostMapping("/profile/edit")
	public String saveProfile(@ModelAttribute("member") Member member, Principal principal,
			RedirectAttributes redirectAttributes) {
		String username = principal.getName(); // Get the current logged-in user's username
		Member existingMember = memberRepository.findByUsername(username);

		// Make sure the existing member was found
		if (existingMember == null) {
			redirectAttributes.addFlashAttribute("error", "Member not found.");
			return "redirect:/profile"; // Redirect back to the profile page
		}

		// Keep the old password (don't overwrite it)
		member.setPassword(existingMember.getPassword());

		// You can also retain other necessary fields like the ID or role
		member.setId(existingMember.getId()); // Don't overwrite the ID field

		// Save the updated member details
		memberRepository.save(member); // Save updated profile data

		// Redirect back to profile page with a success message
		redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
		return "redirect:/profile";
	}
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
