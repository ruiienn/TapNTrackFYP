package fyp.admin;

import jakarta.persistence.*;


@Entity
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = true)
    private Activities activity;

    @ManyToOne
    @JoinColumn(name = "reward_id", nullable = true)
    private Rewards rewards;

    private int pointsChange;
    private String description;

    private int memberPoints;  // Store member's points at the time of the activity

    public History(Member member, Activities activity, Rewards rewards, int pointsChange, String description) {
        this.member = member;
        this.activity = activity;
        this.rewards = rewards;
        this.pointsChange = pointsChange;
        this.description = description;
        this.memberPoints = member.getPoints();  // Store the points from the member
    }

    public History() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Activities getActivity() {
        return activity;
    }

    public void setActivity(Activities activity) {
        this.activity = activity;
    }

    public Rewards getRewards() {
        return rewards;
    }

    public void setRewards(Rewards rewards) {
        this.rewards = rewards;
    }

    public int getPointsChange() {
        return pointsChange;
    }

    public void setPointsChange(int pointsChange) {
        this.pointsChange = pointsChange;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public int getMemberPoints() {
		return memberPoints;
	}

	public void setMemberPoints(int memberPoints) {
		this.memberPoints = memberPoints;
	}
    
}

