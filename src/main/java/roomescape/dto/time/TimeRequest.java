package roomescape.dto.time;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;
import roomescape.domain.time.Time;

import java.time.LocalTime;

public record TimeRequest(
        @DateTimeFormat(pattern = "kk:mm")
        @NonNull
        LocalTime startAt
) {

    public Time toTime() {
        return new Time(this.startAt);
    }
}
