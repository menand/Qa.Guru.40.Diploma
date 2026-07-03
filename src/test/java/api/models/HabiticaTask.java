package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HabiticaTask {
    private String id;
    private String text;
    private TaskType type;
    private String notes;
    private Boolean completed;
    private Double priority;
}
