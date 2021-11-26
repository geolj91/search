package com.search.repository;

import com.search.document.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MediaRepository extends ElasticsearchRepository<Media, String> {
    List<Media> findByName(String name);

    List<Media> findByNameContaining(String name, Sort sort);
}
