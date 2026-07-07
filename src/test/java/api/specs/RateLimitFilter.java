package api.specs;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Habitica ограничивает клиента 30 запросами в минуту (анонимные запросы — по IP).
 * Когда лимит почти исчерпан — досыпаем до конца текущего окна по X-RateLimit-Reset,
 * чтобы тесты не падали с 429 Too Many Requests. Повторить запрос внутри фильтра
 * нельзя (цепочка фильтров REST Assured одноразовая), поэтому холодный старт JVM
 * с уже исчерпанным окном обрабатывается в {@link helpers.TestUsers}.
 */
public class RateLimitFilter implements Filter {

    /** Формат X-RateLimit-Reset: «Tue Jul 07 2026 20:06:44 GMT+0000 (Coordinated Universal Time)». */
    private static final DateTimeFormatter RESET_FORMAT =
            DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'xx", Locale.ENGLISH);
    private static final long FULL_WINDOW_MILLIS = 61_000;
    private static final long RESET_BUFFER_MILLIS = 1_500;

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);
        String remaining = response.getHeader("X-RateLimit-Remaining");
        boolean exhausted = remaining != null && Double.parseDouble(remaining) <= 1;
        if (exhausted || response.statusCode() == 429) {
            sleep(millisUntilReset(response));
        }
        return response;
    }

    private static long millisUntilReset(Response response) {
        String reset = response.getHeader("X-RateLimit-Reset");
        if (reset != null) {
            try {
                String cleaned = reset.replaceAll("\\s*\\(.*\\)$", "");
                Instant resetAt = ZonedDateTime.parse(cleaned, RESET_FORMAT).toInstant();
                long millis = resetAt.toEpochMilli() - Instant.now().toEpochMilli();
                if (millis > 0 && millis <= FULL_WINDOW_MILLIS) {
                    return millis + RESET_BUFFER_MILLIS;
                }
            } catch (RuntimeException ignored) {
                // формат заголовка изменился — пересидим полное окно
            }
        }
        return FULL_WINDOW_MILLIS;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
