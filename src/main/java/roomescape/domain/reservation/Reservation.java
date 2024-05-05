package roomescape.domain.reservation;

import io.micrometer.common.util.StringUtils;
import roomescape.domain.theme.Theme;
import roomescape.domain.time.Time;
import roomescape.global.exception.error.ErrorType;
import roomescape.global.exception.model.ValidateException;

import java.time.LocalDate;

public class Reservation {

    private final Long id;
    private final String name;
    private final LocalDate date;
    private final Time time;
    private final Theme theme;

    public Reservation(final String name, final LocalDate date, final Time time, final Theme theme) {
        this(null, name, date, time, theme);
    }

    public Reservation(final Long id, final String name, final LocalDate date, final Time time, final Theme theme) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.theme = theme;

        if (StringUtils.isBlank(name) || date == null || time == null || theme == null) {
            throw new ValidateException(ErrorType.INVALID_ERROR, String.format("유효하지 않은 값입니다.%n%s", this.toString()));
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", theme=" + theme +
                '}';
    }
}
