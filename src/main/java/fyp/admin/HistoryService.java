package fyp.admin;

import org.springframework.beans.factory.annotation.Autowired;

public class HistoryService {

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private MemberRepository memberRepository;

	public void recordActivity(Member member, Activities activity) {
	    int pointsRewarded = activity.getPointsRewarded();
	    member.setPoints(member.getPoints() + pointsRewarded);

	    History history = new History(member, activity, null, pointsRewarded,
	            "Points earned at " + activity.getActivity() + " (Participated)");
	    historyRepository.save(history);
	    memberRepository.save(member);
	}

	public void recordRewardRedemption(Member member, Rewards rewards) {
		int pointsRequired = rewards.getPointsRequired();
		member.setPoints(member.getPoints() - pointsRequired);

		History history = new History(member, null, rewards, -pointsRequired,
				"Points deducted for " + rewards.getDescription());
		historyRepository.save(history);
		memberRepository.save(member);
	}
}
