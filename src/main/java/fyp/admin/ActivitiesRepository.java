package fyp.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivitiesRepository extends JpaRepository<Activities, Long> {

    @Query("SELECT a.name, COUNT(p.id) FROM Activity a JOIN Participation p ON a.id = p.activity.id GROUP BY a.name")
    List<Object[]> findParticipantCountsByDate();
}
