package com.gilbert.loadtest.like;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/songs/{id}/like")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void like(@PathVariable Long id) {
        likeService.like(id);
    }
}
