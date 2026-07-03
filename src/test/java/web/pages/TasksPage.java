package web.pages;

import api.models.TaskType;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Главная страница приложения с четырьмя колонками задач:
 * habit, daily, todo, reward.
 */
public class TasksPage {

    private final SelenideElement searchInput = $("input.input-search");
    private final SelenideElement characterName = $("h3.character-name");

    private SelenideElement quickAddField(TaskType type) {
        return $(".tasks-column." + type.key() + " textarea.quick-add");
    }

    private ElementsCollection tasks(TaskType type) {
        return $$(".tasks-column." + type.key() + " .task");
    }

    private SelenideElement taskCard(TaskType type, String text) {
        return tasks(type).findBy(text(text));
    }

    @Step("Дождаться загрузки страницы задач")
    public TasksPage waitLoaded() {
        // первая загрузка SPA у свежего пользователя на живом стенде бывает долгой
        $$(".tasks-column").shouldHave(CollectionCondition.size(4), Duration.ofSeconds(45));
        return this;
    }

    @Step("Проверить имя персонажа в шапке: «{expected}»")
    public TasksPage checkCharacterName(String expected) {
        characterName.shouldHave(text(expected), Duration.ofSeconds(20));
        return this;
    }

    @Step("Создать задачу типа {type} через quick-add: «{text}»")
    public TasksPage addTask(TaskType type, String text) {
        quickAddField(type).setValue(text).pressEnter();
        return this;
    }

    @Step("Проверить, что задача {type} «{text}» отображается в колонке")
    public TasksPage checkTaskVisible(TaskType type, String text) {
        taskCard(type, text).shouldBe(visible);
        return this;
    }

    @Step("Проверить, что задача {type} «{text}» скрыта")
    public TasksPage checkTaskHidden(TaskType type, String text) {
        taskCard(type, text).shouldNotBe(visible);
        return this;
    }

    @Step("Проверить, что задачи {type} «{text}» не существует")
    public TasksPage checkTaskAbsent(TaskType type, String text) {
        taskCard(type, text).shouldNot(exist);
        return this;
    }

    @Step("Выполнить todo «{text}» кликом по чекбоксу")
    public TasksPage completeTodo(String text) {
        taskCard(TaskType.TODO, text).$(".left-control").click();
        return this;
    }

    @Step("Открыть редактирование задачи «{text}»")
    public TaskEditModal openTaskEdit(TaskType type, String text) {
        taskCard(type, text).$("h3.task-title").click();
        return new TaskEditModal();
    }

    @Step("Удалить задачу «{text}» через окно редактирования")
    public TasksPage deleteTask(TaskType type, String text) {
        taskCard(type, text).$("h3.task-title").click();
        $(".modal-dialog").shouldBe(visible)
                .$(".delete-text").click();
        // подтверждение удаления в модалке bootstrap-vue
        $("#delete-task-confirm-modal").shouldBe(visible)
                .$$("button").findBy(text("Delete")).click();
        $("#delete-task-confirm-modal").shouldNotBe(visible);
        return this;
    }

    @Step("Найти задачи по тексту «{query}»")
    public TasksPage search(String query) {
        searchInput.setValue(query);
        return this;
    }

    @Step("Открыть меню пользователя и выйти из аккаунта")
    public HomePage logout() {
        $$(".item-user").filterBy(visible).first().click();
        $$(".topbar-dropdown-item").filterBy(visible).findBy(text("Log Out")).click();
        return new HomePage();
    }

    /**
     * Модальное окно редактирования задачи.
     */
    public static class TaskEditModal {
        private final SelenideElement modal = $(".modal-dialog");
        private final SelenideElement titleInput = $(".modal-dialog .input-title");

        @Step("Изменить заголовок задачи на «{newTitle}» и сохранить")
        public void changeTitleAndSave(String newTitle) {
            titleInput.shouldBe(visible)
                    .setValue(newTitle)
                    .shouldHave(value(newTitle));
            $$(".modal-dialog button").findBy(text("Save")).click();
            modal.shouldNotBe(visible);
        }
    }
}
