package helpers;

import net.datafaker.Faker;

/**
 * Генерация тестовых данных задач и тегов.
 * Данные пользователей — в {@link TestUsers}.
 */
public final class TestData {

    private static final Faker FAKER = new Faker();

    private TestData() {
    }

    public static String randomTaskText() {
        return "task-" + FAKER.regexify("[a-z0-9]{8}");
    }

    /** Текст задачи с заданным префиксом — для проверок поиска/фильтрации. */
    public static String taskTextWithPrefix(String prefix) {
        return prefix + "-" + FAKER.regexify("[a-z0-9]{6}");
    }

    public static String randomTaskTitle() {
        return "Дипломная задача: " + FAKER.book().title();
    }

    public static String randomTagName() {
        return "tag-" + FAKER.color().name();
    }

    public static String randomTagNewName() {
        return "renamed-" + FAKER.color().name();
    }
}
