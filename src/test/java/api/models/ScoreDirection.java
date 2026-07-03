package api.models;

/** Направление скоринга задачи: up — засчитать, down — отменить/штраф. */
public enum ScoreDirection {
    UP, DOWN;

    public String key() {
        return name().toLowerCase();
    }
}
