package api.models;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Типы задач Habitica. key() — значение, которое использует и REST API,
 * и css-классы колонок в вебе (.tasks-column.todo и т.п.).
 */
public enum TaskType {
    HABIT, DAILY, TODO, REWARD;

    @JsonValue
    public String key() {
        return name().toLowerCase();
    }
}
