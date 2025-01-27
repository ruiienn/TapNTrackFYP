package fyp.admin;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class PointsRewarded {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Integer id;
	private String boothForm;
    private String activityForm;
    private String pointsRewardedForm;

	
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

}
