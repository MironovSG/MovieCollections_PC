package ru.mironov.moviecollections.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import ru.mironov.moviecollections.entity.Movie;
import ru.mironov.moviecollections.entity.MovieDetails;
import ru.mironov.moviecollections.entity.User;
import ru.mironov.moviecollections.repository.MovieDetailsRepository;
import ru.mironov.moviecollections.repository.MovieRepository;
import ru.mironov.moviecollections.repository.GenreRepository;
import ru.mironov.moviecollections.repository.UserRepository;
import ru.mironov.moviecollections.service.ActionLogService;
import ru.mironov.moviecollections.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class MovieController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieDetailsRepository movieDetailsRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ActionLogService actionLogService;
    @Autowired
    private UserService userService;
    @GetMapping("/movies")
    public ModelAndView getAllMovies(HttpServletRequest request){
        ModelAndView mav = new ModelAndView("list-movies");
        if(userService.getCurUserRole().equals("USER")) {
            Long curUserId = userService.getCurUserId();
            mav.addObject("movies", movieRepository.findByCreatedBy(curUserId));
        } else {
            mav.addObject("movies", movieRepository.findAll());
            mav.addObject("users", userRepository);
        }
        mav.addObject("pageTitle", "Список фильмов");
        return mav;
    }
    @GetMapping("/addMovieForm")
    public ModelAndView addMovieForm(HttpServletRequest request) {
        int year = Year.now().getValue();
        ModelAndView mav = new ModelAndView("add-movie-form");
        Movie movie = new Movie();
        mav.addObject("movie", movie);
        mav.addObject("genres", genreRepository.findAll());
        mav.addObject("curyear", year);
        mav.addObject("pageTitle", "Добавить фильм");
        return mav;
    }
    @PostMapping("/saveMovie")
    public String saveMovie(@ModelAttribute Movie movie,
                           @RequestParam("movieGenreId") Long genreId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User curUser = userRepository.findByUsername(auth.getName());
        if(movie.getCreatedBy() == null)
            movie.setCreatedBy(curUser.getId());
        movie.setGenre(movieRepository, genreRepository, genreId);
        movieRepository.save(movie);
        actionLogService.logging("Сохранение фильма \""+ movie.getMovieName() + "\" (ID: " + movie.getMovieId() + ")");
        return "redirect:/movies";
    }
    @GetMapping("/updateMovie")
    public ModelAndView updateMovie(@RequestParam Long movieId) throws ModelAndViewDefiningException {
        int year = Year.now().getValue();
        ModelAndView mav = new ModelAndView("add-movie-form");
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        Movie movie = new Movie();
        if(optionalMovie.isPresent()){
            movie = optionalMovie.get();
            if(movie.getCreatedBy().equals(userService.getCurUserId()) || userService.getCurUserRole().equals("ADMIN")) {
                Long genreId = movie.getGenres().get(0).getId();
                String details = "";
                List<MovieDetails> gd = movie.getDetails();
                for (MovieDetails d : gd) {
                    if (!details.equals("")) details += ",";
                    details += d.getDetailId();
                }
                mav.addObject("movie", movie);
                mav.addObject("movieGenre", genreId);
                mav.addObject("details", details);
                mav.addObject("genres", genreRepository.findAll());
                mav.addObject("curyear", year);
                mav.addObject("pageTitle", "Изменить фильм");
            } else {
                throw new ModelAndViewDefiningException(mav);
            }
        } else {
            throw new ModelAndViewDefiningException(mav);
        }
        return mav;
    }
    @GetMapping("/deleteMovie")
    public String deleteMovie(@RequestParam Long movieId){
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        if(optionalMovie.isPresent()) {
            Movie movie = optionalMovie.get();
            if(movie.getCreatedBy().equals(userService.getCurUserId()) || userService.getCurUserRole().equals("ADMIN")) {
                movie.getDetails().clear();
                actionLogService.logging("Удаление фильма \"" + movie.getMovieName() + "\" (ID: " + movie.getMovieId() + ")");
                movieRepository.deleteById(movieId);
            }
        }
        return "redirect:/movies";
    }
}
