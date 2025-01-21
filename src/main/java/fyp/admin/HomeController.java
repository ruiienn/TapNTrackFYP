package fyp.admin;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @GetMapping("/")
    public String getLeaderboard(Model model) {
        List<Member> members = memberRepository.findAll(); // Fetch all members
        Stream<Member> topMembers = members.stream().filter(member -> "ROLE_USER".equals(member.getRole())); // Only include ROLE_USER
        model.addAttribute("topMembers", topMembers.toList()); // Convert stream to list for the model
        return "index";
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        List<Member> topMembers = memberRepository.findAll();
        model.addAttribute("topMembers", topMembers);
        return "leaderboard";
    }
    
    @GetMapping("/history")
    public String showHistoryPage(Model model) {
       

        // Fetch a generic member's points (for instance, a public user or guest)
        Member guestMember = memberRepository.findByUsername("guest");  // Example of a default member
        if (guestMember != null) {
            model.addAttribute("memberPoints", guestMember.getPoints());
        } else {
            model.addAttribute("memberPoints", 0);  // Default points if guest member doesn't exist
        }
        return "history";  // Return the history view
    }

    @GetMapping("/403")
    public String error403() {
        return "403";
    }
}
