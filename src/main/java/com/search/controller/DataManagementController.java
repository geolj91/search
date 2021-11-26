package com.search.controller;

import com.search.document.Media;
import com.search.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@RestController
@RequestMapping("/api/data")
public class DataManagementController {
    private static final String COMMA_DELIMITER = ",";

    private final MediaService service;

    @Autowired
    public DataManagementController(MediaService service) {
        this.service = service;
    }

    @PostMapping
    public void initData() {
        initDataFromCsvFile();
    }

    @PostConstruct
    public void initDataFromCsvFile() {
        service.deleteAll();
        service.bulkSave(prepareDataset());
    }

    private List<Media> prepareDataset() {
        Resource resource = new ClassPathResource("imdb.csv");
        List<Media> mediaList = new ArrayList<>();

        try (Scanner scanner = new Scanner(resource.getInputStream())) {
            int lineNo = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (lineNo == 0) {
                    lineNo++;
                    continue;
                }

                Optional<Media> media = csvRowToMedia(line, lineNo);
                if (media.isPresent()) mediaList.add(media.get());
                lineNo++;
            }
        } catch (Exception e) {
            // TODO: Log error
        }

        return mediaList;
    }

    private Optional<Media> csvRowToMedia(final String line, final int index) {
        String[] mediaData = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        double rating = -1;

        if (!mediaData[2].equals("No Rate")) {
            rating = Double.parseDouble(mediaData[2]);
        }

        if (mediaData.length == 14) {
            Media media = new Media(
                    index,
                    mediaData[0],
                    Integer.parseInt(mediaData[1].trim()),
                    rating,
                    mediaData[4].replaceAll("^\"|\"$", ""),
                    mediaData[6]
            );

            return Optional.of(media);
        }

        return Optional.empty();
    }
}
