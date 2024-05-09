package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.global.exception.model.AssociatedDataExistsException;
import roomescape.global.exception.model.DataDuplicateException;
import roomescape.reservation.dao.ReservationDao;
import roomescape.reservation.dao.TimeDao;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.dto.request.ReservationTimeRequest;
import roomescape.reservation.service.ReservationTimeService;
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.domain.Theme;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import({ReservationTimeService.class, TimeDao.class, ReservationDao.class, ThemeDao.class})
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class ReservationReservationTimeServiceTest {

    @Autowired
    private ReservationTimeService reservationTimeService;
    @Autowired
    private TimeDao timeDao;
    @Autowired
    private ReservationDao reservationDao;
    @Autowired
    private ThemeDao themeDao;

    @Test
    @DisplayName("중복된 예약 시간을 등록하는 경우 예외가 발생한다.")
    void duplicateTimeFail() {
        // given
        timeDao.insert(new ReservationTime(LocalTime.of(12, 30)));

        // when & then
        assertThatThrownBy(() -> reservationTimeService.addTime(new ReservationTimeRequest(LocalTime.of(12, 30))))
                .isInstanceOf(DataDuplicateException.class);
    }

    @Test
    @DisplayName("삭제하려는 시간에 예약이 존재하면 예외를 발생한다.")
    void usingTimeDeleteFail() {
        // given
        ReservationTime reservationTime = timeDao.insert(new ReservationTime(LocalTime.now()));
        Theme theme = themeDao.insert(new Theme("테마명", "설명", "썸네일URL"));

        // when
        reservationDao.insert(new Reservation(LocalDate.now().plusDays(1L), reservationTime, theme));

        // then
        assertThatThrownBy(() -> reservationTimeService.removeTimeById(reservationTime.getId()))
                .isInstanceOf(AssociatedDataExistsException.class);
    }
}
