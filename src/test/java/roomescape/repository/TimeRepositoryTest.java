package roomescape.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.domain.time.Time;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(TimeRepository.class)
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class TimeRepositoryTest {

    @Autowired
    private TimeRepository timeRepository;

    @Test
    @DisplayName("등록된 시간의 id를 통해 단건 조회할 수 있다.")
    void findTimeById() {
        //given
        timeRepository.insert(new Time(1L, LocalTime.of(17, 30)));

        // when
        Time foundTime = timeRepository.findById(1L);

        // then
        assertThat(foundTime.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("전체 시간 정보를 조회한다.")
    void readDbTimes() {
        // given
        timeRepository.insert(new Time(LocalTime.of(17, 30)));
        timeRepository.insert(new Time(LocalTime.of(19, 30)));

        // when
        List<Time> times = timeRepository.findAll();

        // then
        assertThat(times.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("하나의 시간만 등록한 경우, DB를 조회 했을 때 조회 결과 개수는 1개이다.")
    void postTimeIntoDb() {
        // given
        timeRepository.insert(new Time(1L, LocalTime.of(17, 30)));

        // when
        List<Time> times = timeRepository.findAll();

        // then
        assertThat(times.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("하나의 시간만 등록한 경우, 시간 삭제 뒤 DB를 조회 했을 때 조회 결과 개수는 0개이다.")
    void readTimesSizeFromDbAfterPostAndDelete() {
        // given
        timeRepository.insert(new Time(1L, LocalTime.of(17, 30)));

        // when
        timeRepository.deleteById(1L);
        List<Time> times = timeRepository.findAll();

        // then
        assertThat(times.size()).isEqualTo(0);
    }
}
