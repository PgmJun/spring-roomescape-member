package roomescape.domain.reservation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import roomescape.theme.domain.Theme;
import roomescape.reservation.domain.ReservationTime;
import roomescape.global.exception.model.ValidateException;
import roomescape.reservation.domain.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;

public class ReservationTest {

    @ParameterizedTest
    @MethodSource("validateConstructorParameterBlankSource")
    @DisplayName("객체 생성 시, null 또는 공백이 존재하면 예외를 발생한다.")
    void validateConstructorParameterBlank(String name, LocalDate date, ReservationTime reservationTime, Theme theme) {

        // when & then
        Assertions.assertThatThrownBy(() -> new Reservation(name, date, reservationTime, theme))
                .isInstanceOf(ValidateException.class);
    }

    static Stream<Arguments> validateConstructorParameterBlankSource() {
        return Stream.of(
                Arguments.of("", LocalDate.now(), new ReservationTime(LocalTime.now().plusHours(1)),
                        new Theme("테마명", "설명", "썸네일URI")),
                Arguments.of("", null, new ReservationTime(LocalTime.now().plusHours(1)),
                        new Theme("테마명", "설명", "썸네일URI")),
                Arguments.of("", LocalDate.now(), null,
                        new Theme("테마명", "설명", "썸네일URI")),
                Arguments.of("", LocalDate.now(), new ReservationTime(LocalTime.now().plusHours(1)), null)
        );
    }
}
