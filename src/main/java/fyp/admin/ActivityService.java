package fyp.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    @Autowired
    private ActivitiesRepository activitiesRepository;

    public List<Activities> getAllActivities() {
        return activitiesRepository.findAll();
    }
    
    public List<ParticipationResponse> getParticipationData() {
        List<Object[]> rawData = activitiesRepository.findParticipantCountsByDate();

        return rawData.stream()
                .map(data -> new ParticipationResponse((String) data[0], ((Number) data[1]).intValue()))
                .collect(Collectors.toList());
    }

    public void saveActivity(Activities activities) {
        activitiesRepository.save(activities);
    }

    public void deleteActivity(Long id) {
        activitiesRepository.deleteById(id);
    }

    public Activities getActivityById(Long id) {
        return activitiesRepository.findById(id).orElse(null); // Returns null if not found
    }
}
