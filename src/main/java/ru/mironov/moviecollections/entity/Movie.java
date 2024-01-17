package ru.mironov.moviecollections.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mironov.moviecollections.repository.MovieRepository;
import ru.mironov.moviecollections.repository.GenreRepository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="movieId")
    private long movieId;
    @Column(name="movieName")
    private String movieName;
    @Column(name="movieYear")
    private int movieYear;
    @Column(name="movieDirector")
    private String movieDirector;
    @Column(name="createdBy")
    private Long createdBy;
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "movies_genres",
            joinColumns = { @JoinColumn(name = "movie_id", referencedColumnName = "movieId") },
            inverseJoinColumns = { @JoinColumn(name = "genre_id", referencedColumnName = "id") }
    )
    private List<Genre> genres = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="movie_movie_id")
    private List<MovieDetails> details = new ArrayList<>();
    public void setGenre(MovieRepository movieRepository, GenreRepository genreRepository, Long genreId){
        Optional<Genre> genreO = genreRepository.findById(genreId);
        if (genreO.isPresent()) {
            Genre genre = genreO.get();
            this.getGenres().clear();
            this.getGenres().add(genre);
            movieRepository.save(this);
        }
    }
    public String getGenre(){
        String ret = "";
        List<Genre> genres = this.getGenres();
        if(genres.size() == 0) ret = "";
            else ret = this.getGenres().get(0).getName();
        return ret;
    }
}