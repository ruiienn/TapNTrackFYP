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
        List<Member> members = memberRepository.findAll(); // Fetch all members
        model.addAttribute("members", members); // Add members to model to show points history
        
        // Optionally, include member's history here as well
        // List<History> histories = historyRepository.findAll(); // Fetch all histories
        // model.addAttribute("historyList", histories); // Add history to model

        return "history"; // Return the history view
    }

    @GetMapping("/403")
    public String error403() {
        return "403";
    }
}
