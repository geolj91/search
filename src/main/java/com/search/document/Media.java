package com.search.document;

import com.search.helper.Indices;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = Indices.MEDIA_INDEX)
public class Media {

    @Id
    @Field(type = FieldType.Keyword)
    private long id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Integer)
    private int year;

    @Field(type = FieldType.Double)
    private double rating;

    @Field(type = FieldType.Text)
    private String genre;

    @Field(type = FieldType.Text)
    private String type;


    public Media(long id, String name, int year, double rating, String genre, String type) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.rating = rating;
        this.genre = genre;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int date) {
        this.year = date;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
