package roomescape.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.jdbc.JdbcTestUtils;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.theme.Theme;
import roomescape.domain.time.Time;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class ReservationRepositoryTest {

    private ReservationRepository reservationRepository;
    private TimeRepository timeRepository;
    private ThemeRepository themeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        this.reservationRepository = new ReservationRepository(jdbcTemplate, dataSource);
        this.timeRepository = new TimeRepository(jdbcTemplate, dataSource);
        this.themeRepository = new ThemeRepository(jdbcTemplate, dataSource);
    }

    @Test
    @DisplayName("repository를 통해 조회한 예약 수는 DB를 통해 조회한 예약 수와 같다.")
    void readDbReservations() {
        // given
        Time time = timeRepository.insert(new Time(LocalTime.of(17, 30)));
        Theme theme = themeRepository.insert(new Theme("테마명", "설명", "썸네일URL"));
        reservationRepository.insert(new Reservation(
                "브라운",
                LocalDate.of(2024, 4, 25),
                time,
                theme
        ));

        // when
        List<Reservation> reservations = reservationRepository.findAll();
        int count = JdbcTestUtils.countRowsInTable(jdbcTemplate, "reservation");

        // then
        assertThat(reservations.size()).isEqualTo(count);
    }

    @Test
    @DisplayName("하나의 예약만 등록한 경우, DB를 조회 했을 때 조회 결과 개수는 1개이다.")
    void postReservationIntoDb() {
        // given
        Time time = timeRepository.insert(new Time(LocalTime.of(17, 30)));
        Theme theme = themeRepository.insert(new Theme("테마명", "설명", "썸네일URL"));

        // when
        reservationRepository.insert(new Reservation(
                "브라운",
                LocalDate.of(2024, 4, 25),
                time,
                theme
        ));
        int count = JdbcTestUtils.countRowsInTable(jdbcTemplate, "reservation");

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("하나의 예약만 등록한 경우, 예약 삭제 뒤 DB를 조회 했을 때 조회 결과 개수는 0개이다.")
    void readReservationsSizeFromDbAfterPostAndDelete() {
        // given
        Time time = timeRepository.insert(new Time(LocalTime.of(17, 30)));
        Theme theme = themeRepository.insert(new Theme("테마명", "설명", "썸네일URL"));

        // when
        Reservation savedReservation = reservationRepository.insert(new Reservation(
                "브라운",
                LocalDate.of(2024, 4, 25),
                time,
                theme
        ));
        int deleteCount = reservationRepository.deleteById(savedReservation.getId());

        // then
        assertThat(deleteCount).isEqualTo(1);
    }

    @Test
    @DisplayName("테마ID와 날짜를 통해 예약정보를 조회한다.")
    void readReservationsByThemeIdAndDate() {
        // given
        Time time = timeRepository.insert(new Time(LocalTime.of(17, 30)));
        Theme theme = themeRepository.insert(new Theme("테마명", "설명", "썸네일URL"));
        Reservation savedReservation = reservationRepository.insert(new Reservation(
                "브라운",
                LocalDate.of(2024, 4, 25),
                time,
                theme
        ));

        // when
        List<Reservation> reservations = reservationRepository.findByDateAndThemeId(
                LocalDate.of(2024, 4, 25),
                theme.getId()
        );

        // then
        assertThat(reservations.size()).isEqualTo(1);
        assertThat(reservations.get(0).getId()).isEqualTo(savedReservation.getId());
    }
}
