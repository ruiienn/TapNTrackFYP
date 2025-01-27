package fyp.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PointsRewardedService {
	 @Autowired
	    private PointsRewardedRepository pointsRewardedRepository;

	    public List<PointsRewarded> findAll() {
	        return pointsRewardedRepository.findAll();
	    }

	    public void save(PointsRewarded pointsRewarded) {
	        pointsRewardedRepository.save(pointsRewarded);
	    }
}
