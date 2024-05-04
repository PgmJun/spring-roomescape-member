package roomescape.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.theme.Theme;
import roomescape.domain.time.Time;
import roomescape.dto.time.TimeRequest;
import roomescape.global.exception.model.ConflictException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ThemeRepository;
import roomescape.repository.TimeRepository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class TimeServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    private TimeService timeService;
    private TimeRepository timeRepository;
    private ReservationRepository reservationRepository;
    private ThemeRepository themeRepository;

    @BeforeEach
    void init() {
        timeRepository = new TimeRepository(jdbcTemplate, dataSource);
        reservationRepository = new ReservationRepository(jdbcTemplate, dataSource);
        themeRepository = new ThemeRepository(jdbcTemplate, dataSource);
        timeService = new TimeService(timeRepository, reservationRepository);
    }

    @Test
    @DisplayName("중복된 예약 시간을 등록하는 경우 예외가 발생한다.")
    void duplicateTimeFail() {
        // given
        timeRepository.insert(new Time(LocalTime.of(12, 30)));

        // when & then
        assertThatThrownBy(() -> timeService.addTime(new TimeRequest(LocalTime.of(12, 30))))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("삭제하려는 시간에 예약이 존재하면 예외를 발생한다.")
    void usingTimeDeleteFail() {
        // given
        Time time = timeRepository.insert(new Time(LocalTime.now()));
        Theme theme = themeRepository.insert(new Theme("테마명", "설명", "썸네일URL"));

        // when
        reservationRepository.insert(new Reservation("예약", LocalDate.now().plusDays(1L), time, theme));

        // then
        assertThatThrownBy(() -> timeService.removeTimeById(time.getId()))
                .isInstanceOf(ConflictException.class);
    }
}
