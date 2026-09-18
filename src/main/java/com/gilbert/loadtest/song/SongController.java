package com.gilbert.loadtest.song;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    public List<SongResponse> list(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size,
                                   @RequestParam(defaultValue = "false") boolean nPlusOne) {
        return songService.list(page, size, nPlusOne);
    }

    @GetMapping("/{id}")
    public SongResponse detail(@PathVariable Long id) {
        return songService.detail(id);
    }
}
