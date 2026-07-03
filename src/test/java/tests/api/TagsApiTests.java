package tests.api;

import api.models.TagModel;
import api.steps.TagsApi;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("Теги")
@Link(name = "Habitica API doc", url = "https://habitica.com/apidoc/")
public class TagsApiTests extends ApiTestBase {

    private static final Faker FAKER = new Faker();

    @Test
    @Story("Создание тегов")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Созданный тег возвращается с id и появляется в списке тегов")
    void createdTagAppearsInList() {
        String name = "tag-" + FAKER.color().name();

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
    @Story("Редактирование и удаление тегов")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Тег можно переименовать, после удаления он исчезает из списка")
    void tagCanBeRenamedAndDeleted() {
        TagModel created = TagsApi.createTag(USER, "tag-" + FAKER.animal().name());
        String newName = "renamed-" + FAKER.color().name();

        TagModel renamed = TagsApi.updateTag(USER, created.getId(), newName);
        step("Проверить новое имя тега; id не изменился", () -> {
            assertThat(renamed.getId()).isEqualTo(created.getId());
            assertThat(renamed.getName()).isEqualTo(newName);
        });

        TagsApi.deleteTag(USER, created.getId());
        List<TagModel> tags = TagsApi.getTags(USER);
        step("Проверить, что тег удалён", () ->
                assertThat(tags)
                        .extracting(TagModel::getId)
                        .doesNotContain(created.getId()));
    }
}
