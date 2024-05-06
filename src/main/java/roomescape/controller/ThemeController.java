package roomescape.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.theme.ThemeRequest;
import roomescape.dto.theme.ThemeResponse;
import roomescape.dto.theme.ThemesResponse;
import roomescape.global.dto.response.ApiResponse;
import roomescape.service.ThemeService;

import java.time.LocalDate;

@RestController
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping("/themes")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ThemesResponse> getAllThemes() {

        return ApiResponse.success(themeService.findAllThemes());
    }

    @GetMapping("/themes/top")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ThemesResponse> getTopNThemesBetweenDate(
            @RequestParam final int count,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate startAt,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate endAt
    ) {
        return ApiResponse.success(themeService.findTopNThemes(count, startAt, endAt));
    }

    @PostMapping("/themes")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ThemeResponse> saveTheme(
            @RequestBody final ThemeRequest request,
            HttpServletResponse response
    ) {
        ThemeResponse themeResponse = themeService.addTheme(request);
        response.setHeader(HttpHeaders.LOCATION, "/themes/" + themeResponse.id());

        return ApiResponse.success(themeResponse);
    }

    @DeleteMapping("/themes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> removeTheme(@PathVariable final Long id) {
        themeService.removeThemeById(id);

        return ApiResponse.success();
    }
}
