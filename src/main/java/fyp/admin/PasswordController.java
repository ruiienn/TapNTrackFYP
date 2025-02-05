/**
 * 
 * I declare that this code was written by me, xandr. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: xandra
 * Student ID: 22022591
 * Date created: 2025-Jan-08 4:25:00 pm 
 * 
 */
package fyp.admin;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author xandr
 *
 */

@Controller
public class PasswordController {

    @Autowired
    private MemberRepository memberRepository;

//    @Autowired
//    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @PostMapping("/change_password")
    public String changePassword(@RequestParam String email,
                                 @RequestParam String newPassword,
                                 Model model) {
        // Fetch the user by email
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (!optionalMember.isPresent()) {
            model.addAttribute("error", "User not found.");
            return "change_password"; // Reload forget password page with error
        }

        Member member = optionalMember.get();

        // Encode the new password
        String encodedPassword = passwordEncoder.encode(newPassword);

        // Update the password
        member.setPassword(encodedPassword);
        memberRepository.save(member);

        model.addAttribute("success", "Password updated successfully.");
        return "redirect:/login"; // Redirect to login page
    }
    
//    @PostMapping("/forget-password")
//    public String sendResetEmail(@RequestParam String email, Model model) {
//        // Fetch the user by email
//        Optional<Member> optionalMember = memberRepository.findByEmail(email);
//        if (!optionalMember.isPresent()) {
//            model.addAttribute("error", "Email not found.");
//            return "forget"; // Reload forget password page with error
//        }
//
//        Member member = optionalMember.get();
//
//        // Prepare reset link
//        String resetLink = "http://localhost:8080/forget" + email;
//        String subject = "Password Reset Request";
//        String message = "Dear " + member.getUsername() + ",\n\n" +
//                         "You requested to reset your password. Click the link below to reset it:\n" +
//                         resetLink + "\n\n" +
//                         "If you didn't request this, please ignore this email.\n\n" +
//                         "Best regards,\nTapnTrack";

//        // Send the email
//        emailService.sendEmail(email, subject, message);
//
//        model.addAttribute("success", "A reset link has been sent to your email.");
//        return "forget"; // Display success message on the same page
    }