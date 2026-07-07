package tests.api;

import api.models.TagModel;
import api.steps.TagsApi;
import helpers.TestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("Теги")
@Link(name = "Habitica API doc", url = "https://habitica.com/apidoc/")
public class TagsApiTests extends ApiTestBase {

    @Test
    @Story("Создание тегов")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Созданный тег возвращается с id и появляется в списке тегов")
    void createdTagAppearsInList() {
        String name = TestData.randomTagName();

        TagModel created = TagsApi.createTag(USER, name);
        List<TagModel> tags = TagsApi.getTags(USER);

        step("Проверить созданный тег и его наличие в списке", () -> {
            assertThat(created.getId()).matches(UUID_REGEX);
            assertThat(created.getName()).isEqualTo(name);
            assertThat(tags)
                    .filteredOn(tag -> created.getId().equals(tag.getId()))
                    .singleElement()
                    .satisfies(tag -> assertThat(tag.getName()).isEqualTo(name));
        });
    }

    @Test
    @Story("Редактирование тегов")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("PUT /tags/:id переименовывает тег, id остаётся прежним")
    void renamedTagKeepsId() {
        TagModel created = TagsApi.createTag(USER, TestData.randomTagName());
        String newName = TestData.randomTagNewName();

        TagModel renamed = TagsApi.updateTag(USER, created.getId(), newName);

        step("Проверить новое имя тега; id не изменился", () -> {
            assertThat(renamed.getId()).isEqualTo(created.getId());
            assertThat(renamed.getName()).isEqualTo(newName);
        });
    }

    @Test
    @Story("Удаление тегов")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Удалённый тег исчезает из списка тегов")
    void deletedTagDisappearsFromList() {
        TagModel created = TagsApi.createTag(USER, TestData.randomTagName());

        TagsApi.deleteTag(USER, created.getId());
        List<TagModel> tags = TagsApi.getTags(USER);

        step("Проверить, что тег удалён", () ->
                assertThat(tags)
                        .extracting(TagModel::getId)
                        .doesNotContain(created.getId()));
    }
}
