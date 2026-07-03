package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreResult {
    private Double delta;
    private Double exp;
    private Double gp;
    private Integer lvl;
    private Double hp;
}
