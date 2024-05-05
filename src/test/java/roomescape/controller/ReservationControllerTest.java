package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class ReservationControllerTest {

    @Autowired
    private ReservationController reservationController;

    @LocalServerPort
    private int port;

    private final Map<String, String> timeParams = Map.of("startAt", "17:00");

    private final Map<String, String> themeParams = Map.of(
            "name", "테마명",
            "description", "설명",
            "thumbnail", "썸네일 URL"
    );

    private final Map<String, String> reservationParams = Map.of(
            "name", "썬",
            "date", LocalDate.now().plusDays(1L).toString(),
            "timeId", "1",
            "themeId", "1"
    );

    // TODO: setUp 코드 제거하고 각각 테스트에서 처리
    @BeforeEach
    void setUp() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(timeParams)
                .when().post("/times")
                .then().log().all()
                .statusCode(201)
                .body("data.id", is(1));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(themeParams)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201)
                .body("data.id", is(1));
    }

    @Test
    @DisplayName("처음으로 등록하는 예약의 id는 1이다.")
    void firstPost() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(reservationParams)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("data.id", is(1))
                .header("Location", "/reservations/1");
    }

    @Test
    @DisplayName("아무 예약도 하지 않은 경우, 예약 목록 조회 결과 개수는 0개이다.")
    void readEmptyReservations() {
        RestAssured.given().log().all()
                .port(port)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.reservations.size()", is(0));
    }

    @Test
    @DisplayName("하나의 예약만 등록한 경우, 예약 목록 조회 결과 개수는 1개이다.")
    void readReservationsSizeAfterFirstPost() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(reservationParams)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("data.id", is(1))
                .header("Location", "/reservations/1");

        RestAssured.given().log().all()
                .port(port)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.reservations.size()", is(1));
    }

    @Test
    @DisplayName("하나의 예약만 등록한 경우, 예약 삭제 뒤 예약 목록 조회 결과 개수는 0개이다.")
    void readReservationsSizeAfterPostAndDelete() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(reservationParams)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("data.id", is(1))
                .header("Location", "/reservations/1");

        RestAssured.given().log().all()
                .port(port)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .port(port)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.reservations.size()", is(0));
    }

    @Test
    @DisplayName("컨트롤러에는 JdbcTemplate를 사용한 DB관련 로직이 존재하지 않는다.")
    void jdbcTemplateNotInjected() {
        boolean isJdbcTemplateInjected = false;

        for (Field field : reservationController.getClass().getDeclaredFields()) {
            if (field.getType().equals(JdbcTemplate.class)) {
                isJdbcTemplateInjected = true;
                break;
            }
        }

        assertThat(isJdbcTemplateInjected).isFalse();
    }

    @Test
    @DisplayName("특정 날짜의 특정 테마 예약 현황을 조회한다.")
    void readReservationByDateAndThemeId() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .when().get("/reservations/themes/1/times?date=" + LocalDate.MAX)
                .then().log().all()
                .statusCode(200)
                .body("data.reservationTimes.size()", is(1));
    }

    @ParameterizedTest
    @MethodSource("requestValidateSource")
    @DisplayName("예약 생성 시, 요청 값에 공백 또는 null이 포함되어 있으면 400 에러를 발생한다.")
    void validateBlankRequest(Map<String, String> invalidRequestBody) {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(invalidRequestBody)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 생성 시, 정수 요청 데이터에 문자가 입력되어오면 400 에러를 발생한다.")
    void validateRequestDataFormat() {
        Map<String, String> invalidTypeRequestBody = Map.of(
                "name", "썬",
                "date", LocalDate.now().plusDays(1L).toString(),
                "timeId", "1",
                "themeId", "한글"
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(invalidTypeRequestBody)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    static Stream<Map<String, String>> requestValidateSource() {
        return Stream.of(
                Map.of(
                        "name", "썬",
                        "date", LocalDate.now().plusDays(1L).toString(),
                        "themeId", "1"
                ),
                Map.of(
                        "name", " ",
                        "date", LocalDate.now().plusDays(1L).toString(),
                        "timeId", "1",
                        "themeId", "1"
                ),
                Map.of(
                        "name", "",
                        "date", LocalDate.now().plusDays(1L).toString(),
                        "timeId", " ",
                        "themeId", "1"
                )
        );
    }
}
