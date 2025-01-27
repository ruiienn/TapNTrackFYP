package fyp.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsRewardedRepository extends JpaRepository<PointsRewarded, Integer> {
}



