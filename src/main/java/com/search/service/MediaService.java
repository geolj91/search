package com.search.service;

import com.search.document.Media;
import com.search.helper.Indices;
import com.search.repository.MediaRepository;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.suggest.response.TermSuggestion;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MediaService {
    private static final String FROM_KEY = "from";
    private static final String TO_KEY = "to";
    private static final String GENRE_KEY = "genre";
    private static final String NAME_KEY = "name";
    private static final String YEAR_KEY = "year";
    private static final String RATING_KEY = "rating";
    private static final String RESULTS_KEY = "results";
    private static final String SUGGESTION_KEY = "suggestion";
    private static final String AGGREGATION_NAME = "countByRating";

    private final MediaRepository repository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public MediaService(MediaRepository repository, ElasticsearchOperations elasticsearchOperations) {
        this.repository = repository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public void bulkSave(final List<Media> mediaList) {
        getRepository().saveAll(mediaList);
    }

    public void save(final Media media) {
        getRepository().save(media);
    }

    public void deleteAll() {
        getRepository().deleteAll();
    }

    public Media findById(final String id) {
        return getRepository().findById(id).orElse(null);
    }

    public List<Media> findByName(final String name) {
        return getRepository().findByName(name);
    }

    public List<Media> findByNameContaining(final String name) {
        return getRepository().findByNameContaining(name, Sort.by(Sort.Direction.DESC, YEAR_KEY));
    }

    public Map<String, Object> findByNameWithSuggestion(final String query) {
        Map<String, Object> response = new HashMap<String, Object>();
        List<Media> searchResults = new ArrayList<>();
        String suggestion = "";

        QueryBuilder queryBuilder = QueryBuilders.wildcardQuery(NAME_KEY, "*"+query+"*");
        SuggestionBuilder<TermSuggestionBuilder> suggestionBuilder = new TermSuggestionBuilder(NAME_KEY).text(query);
        SuggestBuilder suggestBuilder = new SuggestBuilder().addSuggestion(SUGGESTION_KEY, suggestionBuilder);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSuggestBuilder(suggestBuilder)
                .withSorts(SortBuilders.fieldSort(YEAR_KEY).order(SortOrder.DESC))
                .build();
        SearchHits<Media> searchResponse = getElasticsearchOperations().search(
                searchQuery,
                Media.class,
                IndexCoordinates.of(Indices.MEDIA_INDEX)
        );

        searchResponse.getSearchHits().forEach(searchHit -> searchResults.add(searchHit.getContent()));
        List termSuggestions = searchResponse.getSuggest().getSuggestion(SUGGESTION_KEY).getEntries();

        if (!termSuggestions.isEmpty()) {
            List<TermSuggestion.Entry.Option> termSuggestionOptions = ((TermSuggestion.Entry) termSuggestions.get(0)).getOptions();

            if (!termSuggestionOptions.isEmpty()) {
                suggestion = termSuggestionOptions.get(0).getText();
            }
        }

        if (searchResults.isEmpty()) {
            response.put(SUGGESTION_KEY, suggestion);
        }
        response.put(RESULTS_KEY, searchResults);

        return response;
    }

    public Map<String, Long> countByRating() {
        Map<String, Long> response = new HashMap<String, Long>();

        RangeAggregationBuilder aggregationBuilder = AggregationBuilders
                .range(AGGREGATION_NAME)
                .field(RATING_KEY)
                .addRange(0, 1.9)
                .addRange(2, 3.9)
                .addRange(4, 5.9)
                .addRange(6, 7.9)
                .addRange(8, 10);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withAggregations(aggregationBuilder)
                .build();

        SearchHits<Media> searchResponse = getElasticsearchOperations().search(
                searchQuery,
                Media.class,
                IndexCoordinates.of(Indices.MEDIA_INDEX)
        );

        List buckets = ((Range) ((Aggregations) searchResponse.
                getAggregations()
                .aggregations())
                .asList()
                .get(0))
                .getBuckets();

        for (Object bucketObject : buckets) {
            Range.Bucket bucket = (Range.Bucket) bucketObject;
            response.put(bucket.getKeyAsString(), bucket.getDocCount());
        }

        return response;
    }

    public Map<String, Object> findByGenreAndRating(final String genre, final double from, final double to) {
        Map<String, Object> response = new HashMap<String, Object>();
        Map<String, Double> rating = new HashMap<String, Double>();
        List<Media> searchResults = new ArrayList<>();

        QueryBuilder query = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.wildcardQuery(GENRE_KEY, "*"+genre+"*"))
                .must(QueryBuilders.rangeQuery(RATING_KEY).from(from).to(to));
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(query)
                .withSorts(SortBuilders.fieldSort(RATING_KEY).order(SortOrder.DESC))
                .build();

        SearchHits<Media> searchResponse = getElasticsearchOperations().search(
                searchQuery,
                Media.class,
                IndexCoordinates.of(Indices.MEDIA_INDEX)
        );

        searchResponse.getSearchHits().forEach(searchHit -> searchResults.add(searchHit.getContent()));
        rating.put(FROM_KEY, from);
        rating.put(TO_KEY, to);
        response.put(GENRE_KEY, genre);
        response.put(RATING_KEY, rating);
        response.put(RESULTS_KEY, searchResults);

        return response;
    }

    public MediaRepository getRepository() {
        return repository;
    }

    public ElasticsearchOperations getElasticsearchOperations() {
        return elasticsearchOperations;
    }
}
