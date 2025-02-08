package fyp.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PointsRewardedService {
    @Autowired
    private PointsRewardedRepository pointsRewardedRepository;

    public Integer calculateTotalPoints(Member member) {
        List<PointsRewarded> pointsHistory = pointsRewardedRepository.findByMember(member);

        if (pointsHistory == null || pointsHistory.isEmpty()) {
            return 0; // No points available
        }

        return pointsHistory.stream()
                .mapToInt(pr -> {
                    try {
                        return Integer.parseInt(pr.getPointsRewardedForm());
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid points value: " + pr.getPointsRewardedForm());
                        return 0;
                    }
                })
                .sum();
    }

    public void save(PointsRewarded pointsRewarded) {
        if (pointsRewarded == null || pointsRewarded.getMember() == null || pointsRewarded.getPointsRewardedForm() == null) {
            throw new IllegalArgumentException("Invalid PointsRewarded object");
        }

        pointsRewardedRepository.save(pointsRewarded);
    }
    
    public void savePointsRewarded(PointsRewarded pointsRewarded) {
        pointsRewardedRepository.save(pointsRewarded);
    }
    
    // New method to fetch points history for a member
    public List<PointsRewarded> getMemberPointsHistory(Member member) {
        return pointsRewardedRepository.findByMember(member);
    }
}
