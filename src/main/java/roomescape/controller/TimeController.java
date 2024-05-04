package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.time.TimeRequest;
import roomescape.dto.time.TimeResponse;
import roomescape.service.TimeService;

import java.net.URI;
import java.util.List;

@RestController
public class TimeController {
    
    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping("/times")
    public ResponseEntity<List<TimeResponse>> readTimes() {
        List<TimeResponse> timeResponses = timeService.findAllTimes();

        return ResponseEntity.ok(timeResponses);
    }

    @PostMapping("/times")
    public ResponseEntity<TimeResponse> createTime(@RequestBody TimeRequest timeRequest) {
        TimeResponse timeResponse = timeService.createTime(timeRequest);

        return ResponseEntity.created(URI.create("/times/" + timeResponse.id()))
                .body(timeResponse);
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable Long id) {
        timeService.deleteTime(id);

        return ResponseEntity.noContent().build();
    }
}
