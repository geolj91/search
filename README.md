# Getting Started

### How to run locally

The project was build using java version 17.0.1

In order to run the application locally, you need to:

1. Download and uncompress elasticsearch on your machine:
[Download](https://www.elastic.co/es/downloads/elasticsearch)

2. Run bin/elasticsearch from the uncompressed files and then make sure elasticsearch is running by entering http://localhost:9200

3. Build the application with Maven:

```shell
mvn clean package
mvn spring-boot:run
```

### Things to keep in mind

1. Data will be saved on elastic search when the application is started.
2. Application will be running at http://localhost:8080
3. Unit tests are not included.


### Available operations and examples

1. Find media by ID: [http://localhost:8080/api/media/1](http://localhost:8080/api/media/1)

2. Search: [http://localhost:8080/api/media/search?query=titanic](http://localhost:8080/api/media/search?query=titanic)

3. Search with suggestion: [http://localhost:8080/api/media/search?query=tatinic](http://localhost:8080/api/media/search?query=tatinic)

4. Count media by rating: [http://localhost:8080/api/media/count/rating](http://localhost:8080/api/media/count/rating)

5. Find media by genre and rating: [http://localhost:8080/api/media/genre?genre=drama&from=9&to=10](http://localhost:8080/api/media/genre?genre=drama&from=9&to=10)
