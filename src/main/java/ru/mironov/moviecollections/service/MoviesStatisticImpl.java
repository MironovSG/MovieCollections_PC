package ru.mironov.moviecollections.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mironov.moviecollections.entity.*;
import ru.mironov.moviecollections.repository.MovieRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MoviesStatisticImpl implements MoviesStatistic{
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    UserService userService;

    public String getStatistic(String statOption){
        String ret = "";
        String retHeader = "";

        Long curUserId = userService.getCurUserId();
        List<Movie> movieList = movieRepository.findByCreatedBy(curUserId);

        Integer copies = 0;
        Float duration = 0.0f;

        switch (statOption) {
            case "general":
                    retHeader = "Общая статистика";

                    ret += getStatLine("Всего фильмов добавлено", String.valueOf(movieList.size()));
                    for (Movie g : movieList) {
                        List<MovieDetails> gD = g.getDetails();
                        for(MovieDetails gDD: gD){
                            copies++;
                            duration += gDD.getDuration();
                        }
                    }
                    ret += getStatLine("Всего записей фильмов", String.valueOf(copies));
                    ret += getStatLine("Продолжительность, мин", String.valueOf(duration));
                    ret = getStatBlock(ret);
                break;

            case "formats":
                    retHeader = "Статистика по форматам";

                    Map<Format, Integer> formatMapCopies = new HashMap<>();
                    Map<Format, Float> formatMapDuration = new HashMap<>();

                    for (Movie g : movieList) {
                        List<MovieDetails> gD = g.getDetails();
                        for(MovieDetails gDD: gD){
                            Format p = gDD.getFormat();
                            if(formatMapCopies.containsKey(p))
                                formatMapCopies.put(p, formatMapCopies.get(p) + 1);
                            else
                                formatMapCopies.put(p, 1);

                            if(formatMapDuration.containsKey(p))
                                formatMapDuration.put(p, formatMapDuration.get(p) + gDD.getDuration());
                            else
                                formatMapDuration.put(p, gDD.getDuration());
                        }
                    }
                    ret = getStatBlockAll(formatMapCopies, formatMapDuration);

                break;
            case "publishers":
                    retHeader = "Статистика по издательствам";

                    Map<Publisher, Integer> publisherMapCopies = new HashMap<>();
                    Map<Publisher, Float> publisherMapDuration = new HashMap<>();

                    for (Movie g : movieList) {
                        List<MovieDetails> gD = g.getDetails();
                        for(MovieDetails gDD: gD){
                            Publisher s = gDD.getPublisher();

                            if(publisherMapCopies.containsKey(s))
                                publisherMapCopies.put(s, publisherMapCopies.get(s) + 1);
                            else
                                publisherMapCopies.put(s, 1);
                            if(publisherMapDuration.containsKey(s))
                                publisherMapDuration.put(s, publisherMapDuration.get(s) + gDD.getDuration());
                            else
                                publisherMapDuration.put(s, gDD.getDuration());

                        }
                    }

                    ret = getStatBlockAll (publisherMapCopies, publisherMapDuration);

                break;
            case "countrys":
                    retHeader = "Статистика по странам";

                    Map<Country, Integer> countryMapCopies = new HashMap<>();
                    Map<Country, Float> countryMapDuration = new HashMap<>();

                    for (Movie g : movieList) {
                        List<MovieDetails> gD = g.getDetails();
                        for(MovieDetails gDD: gD) {
                            Country c = gDD.getCountry();

                            if (countryMapCopies.containsKey(c))
                                countryMapCopies.put(c, countryMapCopies.get(c) + 1);
                            else
                                countryMapCopies.put(c, 1);
                            if (countryMapDuration.containsKey(c))
                                countryMapDuration.put(c, countryMapDuration.get(c) + gDD.getDuration());
                            else
                                countryMapDuration.put(c, gDD.getDuration());
                        }
                    }

                    ret = getStatBlockAll (countryMapCopies, countryMapDuration);
                break;

            case "genres":
                    retHeader = "Статистика по жанрам";

                    Map<Genre, Integer> genreMapCopies = new HashMap<>();
                    Map<Genre, Float> genreMapDuration = new HashMap<>();

                    for (Movie g : movieList) {
                        List<MovieDetails> gD = g.getDetails();
                        Genre genre = g.getGenres().get(0);
                        for (MovieDetails gDD : gD) {
                            if(genreMapCopies.containsKey(genre))
                                genreMapCopies.put(genre, genreMapCopies.get(genre) + 1);
                            else
                                genreMapCopies.put(genre, 1);
                            if(genreMapDuration.containsKey(genre))
                                genreMapDuration.put(genre, genreMapDuration.get(genre) + gDD.getDuration());
                            else
                                genreMapDuration.put(genre, gDD.getDuration());

                        }
                    }
                    ret = getStatBlockAll(genreMapCopies, genreMapDuration);
                break;
        }

        ret = "<h3>" + retHeader + "</h3>" + ret;

        return ret;
    }

    private String getStatLine(String header, String value) {
        return header + ": <strong>" + value + "</strong><br>";
    }

    private String getStatBlock(String text) {
        return "<div class=\"alert alert-success\" role=\"alert\">" +
                text +
                "</div>";
    }

    private String getStatBlockAll(Map<?, Integer> copiesMap, Map<?, Float> durationMap) {
        String ret = "";
        Integer copies;
        Float duration;

        for(Map.Entry<?, Integer> entry: copiesMap.entrySet()) {
            String local = "";

            Object key = entry.getKey();
            copies = entry.getValue();
            duration = durationMap.get(key);

            local += getStatLine("<strong>" + key.toString() + "</strong>", "");
            local += getStatLine("Количество записей", String.valueOf(copies));
            local += getStatLine("Продолжительность, мин", String.valueOf(duration));

            ret += getStatBlock(local);
        }

        if(ret.isEmpty()) ret = "Нечего отображать";

        return ret;
    }
}
