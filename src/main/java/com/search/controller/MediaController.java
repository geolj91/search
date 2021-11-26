package com.search.controller;

import com.search.document.Media;
import com.search.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final MediaService service;

    @Autowired
    public MediaController(MediaService service) {
        this.service = service;
    }

    @PostMapping
    public void save(@RequestBody final Media media) {
        service.save(media);
    }

    @GetMapping("/{id}")
    public Media findById(@PathVariable final String id) {
        return service.findById(id);
    }

    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam final String query) {
        return service.findByNameWithSuggestion(query);
    }

    @GetMapping("/search2")
    public List<Media> search2(@RequestParam final String query) {
        return service.findByNameContaining(query);
    }

    @GetMapping("/count/rating")
    public Map<String, Long> countByRating() {
        return service.countByRating();
    }

    @GetMapping("/genre")
    public Map<String, Object> findByGenreAndRating(
            @RequestParam final String genre,
            @RequestParam final double from,
            @RequestParam final double to
    ) {
        return service.findByGenreAndRating(genre, from, to);
    }
}
