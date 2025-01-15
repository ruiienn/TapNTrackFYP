/**
 * 
 * I declare that this code was written by me, xandr. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: xandra
 * Student ID: 22022591
 * Date created: 2024-Nov-13 4:20:33 pm 
 * 
 */
package fyp.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

/**
 * @author xandr
 *
 */
@Controller
public class ActivitiesController {
	@Autowired
	private ActivitiesRepository activitiesRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private HistoryRepository historyRepository;

	@GetMapping("/activities")
	public String viewActivities(Model model) {

		List<Activities> listActivities = activitiesRepository.findAll();

		model.addAttribute("listActivities", listActivities);
		return "activities";
	}

	@GetMapping("/activities/add")
	public String addActivities(Model model) {
		model.addAttribute("activities", new Activities());

		List<Activities> catList = activitiesRepository.findAll();
		model.addAttribute("catList", catList);

		return "add_activities";
	}

	@PostMapping("/activities/save")
	public String saveActivities(Activities activities, @RequestParam("activitiesImages") MultipartFile imagesFile) {

		String imagesName = imagesFile.getOriginalFilename();

		// Set the image name in the activities object
		activities.setImagesName(imagesName);
		Activities savedActivities = activitiesRepository.save(activities);

		try {
			// Preparing the directory path
			String uploadDir = "uploads/activities/" + savedActivities.getId(); // Assuming getId() is a method in your
																				// activities class
			// to get the ID
			Path uploadPath = Paths.get(uploadDir);
			System.out.println("Directory path: " + uploadPath);

			// Checking if the upload path exists, if not it will be created.
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			// Prepare path for the file
			Path fileToCreatePath = uploadPath.resolve(imagesName);
			System.out.println("File path: " + fileToCreatePath);

			// Copy the file to the upload location
			Files.copy(imagesFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);

			// Save the activities object to the database after successfully saving the file
			/* activities savedActivities = activitiesRepository.save(activities); */

		} catch (IOException io) {
			io.printStackTrace();
		}

		return "redirect:/activities";
	}

	@GetMapping("/activities/edit/{id}")
	public String editActivities(@PathVariable("id") Integer id, Model model) {
		Activities activities = activitiesRepository.getReferenceById(id);
		model.addAttribute("activities", activities);
		return "edit_activities";
	}

	@PostMapping("/activities/edit/{id}")
	public String saveUpdatedActivities(@PathVariable("id") Integer id, Activities activities,
			@RequestParam("activitiesImages") MultipartFile imagesFile) {

		// if imagesFile is NOT empty, proceed to:
		// set new images name for activities object, save the activities object, upload
		// new file and
		// update db
		// otherwise, this means no new file was uploaded, simply save the activities
		// object

		if (!imagesFile.isEmpty()) {
			String imagesName = imagesFile.getOriginalFilename();
			System.out.println("Image name from imagesFile: " + imagesName);

			// Set the image name in activities object
			activities.setImagesName(imagesName);

			Activities savedActivities = activitiesRepository.save(activities);

			try {
				// Preparing the directory path
				String uploadDir = "uploads/activities/" + savedActivities.getId();
				Path uploadPath = Paths.get(uploadDir);
				System.out.println("Directory path: " + uploadPath);

				// Checking if the upload path exists, if not it will be created.
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				// Prepare path for file
				Path fileToCreatePath = uploadPath.resolve(imagesName);
				System.out.println("File path: " + fileToCreatePath);

				// Copy file to the upload location
				Files.copy(imagesFile.getInputStream(), fileToCreatePath, StandardCopyOption.REPLACE_EXISTING);

			} catch (IOException io) {
				io.printStackTrace();
			}
		} else { // No edit to image, save the activities object as passed.

			System.out.println("Image name from activities object: " + activities.getImagesName());
			activitiesRepository.save(activities);
		}

		return "redirect:/activities/" + id;
	}

	// delete activities
	@GetMapping("/activities/delete/{id}")
	public String deleteActivities(@PathVariable("id") Integer id) {

		activitiesRepository.deleteById(id);

		return "redirect:/activities";
	}

	// View single activities
	@GetMapping("/activities/{id}")
	public String viewSingleActivities(@PathVariable("id") Integer id, Model model) {

		Activities activities = activitiesRepository.getReferenceById(id);
		model.addAttribute("activities", activities);

		return "view_single_activities";
	}

	/*
	 * @PostMapping("/activities/complete/{memberId}/{activityId}") public String
	 * completeActivity(@PathVariable("memberId") Integer memberId,
	 * 
	 * @PathVariable("activityId") Integer activityId) { Member member =
	 * memberRepository.findById(memberId) .orElseThrow(() -> new
	 * IllegalArgumentException("Invalid member ID: " + memberId)); Activities
	 * activity = activitiesRepository.findById(activityId) .orElseThrow(() -> new
	 * IllegalArgumentException("Invalid activity ID: " + activityId));
	 * 
	 * member.setTotalPoints(member.getTotalPoints() + activity.getPoints());
	 * memberRepository.save(member);
	 * 
	 * History history = new History(); history.setMember(member);
	 * history.setActivity(activity); history.setPoints(activity.getPoints());
	 * history.setAddition(true); historyRepository.save(history);
	 * 
	 * return "Activity completed successfully!"; }
	 */

}
