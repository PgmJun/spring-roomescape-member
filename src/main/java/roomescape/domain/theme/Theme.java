package roomescape.domain.theme;

import io.micrometer.common.util.StringUtils;
import roomescape.global.exception.error.ErrorType;
import roomescape.global.exception.model.ValidateException;

public class Theme {

    private final Long id;
    private final String name;
    private final String description;
    private final String thumbnail;

    public Theme(final String name, final String description, final String thumbnail) {
        this(null, name, description, thumbnail);
    }

    public Theme(final Long id, final Theme theme) {
        this(id, theme.name, theme.description, theme.thumbnail);
    }

    public Theme(final Long id, final String name, String description, final String thumbnail) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;

        if (StringUtils.isBlank(name) || StringUtils.isBlank(description) || StringUtils.isBlank(thumbnail)) {
            throw new ValidateException(ErrorType.INVALID_ERROR, String.format("유효하지 않은 값입니다.%n%s", this.toString()));
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public String toString() {
        return "Theme{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
