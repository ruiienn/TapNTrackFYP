package fyp.admin;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class PointsRewarded {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotEmpty(message = "Booth cannot be empty!")
	private String boothForm;

	@NotEmpty(message = "Activity cannot be empty!")
	private String activityForm;

	@NotEmpty(message = "Points cannot be empty!")
	private String pointsRewardedForm;

	@ManyToOne // Add a relationship to the Member entity
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	// Getters and Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBoothForm() {
		return boothForm;
	}

	public void setBoothForm(String boothForm) {
		this.boothForm = boothForm;
	}

	public String getActivityForm() {
		return activityForm;
	}

	public void setActivityForm(String activityForm) {
		this.activityForm = activityForm;
	}

	public String getPointsRewardedForm() {
		return pointsRewardedForm;
	}

	public void setPointsRewardedForm(String pointsRewardedForm) {
		this.pointsRewardedForm = pointsRewardedForm;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
}
