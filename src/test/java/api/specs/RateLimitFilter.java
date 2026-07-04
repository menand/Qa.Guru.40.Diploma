package api.specs;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * Habitica ограничивает клиента 30 запросами в минуту (анонимные запросы — по IP).
 * Когда лимит почти исчерпан — ждём начала следующего окна, чтобы тесты
 * не падали с 429 Too Many Requests. Повторить запрос внутри фильтра нельзя
 * (цепочка фильтров REST Assured одноразовая), поэтому холодный старт JVM
 * с уже исчерпанным окном обрабатывается в {@link helpers.TestUsers}.
 */
public class RateLimitFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);
        String remaining = response.getHeader("X-RateLimit-Remaining");
        if (remaining != null && Double.parseDouble(remaining) <= 1) {
            // ждём окно целиком; точный парсинг X-RateLimit-Reset — если прогон станет медленным
            sleep(31_000);
        }
        return response;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
