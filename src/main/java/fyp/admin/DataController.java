package fyp.admin;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DataController {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@GetMapping("/activities")
	public List<Map<String, Object>> getActivities() {
		String sql = "SELECT `id`, `activity`, `booth`, `description`, `diploma`, `images_name`, `points` FROM `fyp`.`activities`;";
		return jdbcTemplate.queryForList(sql);
	}

	@GetMapping("/members")
	public List<Map<String, Object>> getMembers() {
		String sql = "SELECT `id`, `avatar`, `email`, `password`, `points`, `role`, `username` FROM `fyp`.`member`;";
		return jdbcTemplate.queryForList(sql);
	}

	@GetMapping("/rewards")
	public List<Map<String, Object>> getRewards() {
		String sql = "SELECT `rewards_id`, `description`, `img_name`, `points_required`, `quantity`, `status` FROM `fyp`.`rewards`;";
		return jdbcTemplate.queryForList(sql);
	}

}
