package roomescape.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.theme.ThemeRequest;
import roomescape.dto.theme.ThemeResponse;
import roomescape.service.ThemeService;

@RestController
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping("/themes")
    public ResponseEntity<List<ThemeResponse>> readThemes() {
        List<ThemeResponse> themeResponses = themeService.findAllThemes();

        return ResponseEntity.ok(themeResponses);
    }

    @GetMapping("/themes/top")
    public ResponseEntity<List<ThemeResponse>> readTopNThemes(@RequestParam int count) {
        List<ThemeResponse> themeResponses = themeService.findTopNThemes(count);

        return ResponseEntity.ok(themeResponses);
    }

    @PostMapping("/themes")
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody ThemeRequest request) {
        ThemeResponse response = themeService.createTheme(request);

        return ResponseEntity.created(URI.create("/themes/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable Long id) {
        themeService.deleteTheme(id);

        return ResponseEntity.noContent().build();
    }
}
