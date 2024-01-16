package ru.mironov.moviecollections.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mironov.moviecollections.entity.*;
import ru.mironov.moviecollections.repository.*;
import ru.mironov.moviecollections.service.ActionLogService;
import ru.mironov.moviecollections.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Controller
public class MovieDetailsController {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieDetailsRepository movieDetailsRepository;
    @Autowired
    private FormatRepository formatRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ActionLogService actionLogService;
    @Autowired
    private UserService userService;

    @GetMapping("/movieDetails")
    public ModelAndView getMovieDetails(HttpServletRequest request, @RequestParam Long movieId) throws ModelAndViewDefiningException {
        ModelAndView mav = new ModelAndView("movie-details");

        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        Movie movie = new Movie();
        if(optionalMovie.isPresent()) {
            movie = optionalMovie.get();

            //if(game.getCreatedBy().equals(userService.getCurUserId()) || userService.getCurUserRole().equals("ADMIN")) {

                mav.addObject("movie", movie);
                mav.addObject("movieDetails", movie.getDetails());
                mav.addObject("formats", formatRepository.findAll());
                mav.addObject("publishers", publisherRepository.findAll());
                mav.addObject("countrys", countryRepository.findAll());
                mav.addObject("pageTitle", "Детали фильма \"" + movie.getMovieName() + "\"");
            //} else {
            //    throw new ModelAndViewDefiningException(mav);
            //}
        } else {
            throw new ModelAndViewDefiningException(mav);
        }
        return mav;
    }

    @PostMapping("/movieDetails/addDetail")
    public String saveMovieDetails(@ModelAttribute MovieDetails movieDetails,
                                   @RequestParam Long movieId,
                                   @RequestParam("movieFormatId") Long formatId,
                                   @RequestParam("moviePublisherId") Long publisherId,
                                   @RequestParam("movieCountryId") Long countryId) {
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        Movie movie;
        if(optionalMovie.isPresent()) {
            movie = optionalMovie.get();

            if(movie.getCreatedBy().equals(userService.getCurUserId()) || userService.getCurUserRole().equals("ADMIN")) {
                Optional<Format> optionalFormat = formatRepository.findById(formatId);
                if (optionalFormat.isPresent()) {
                    Format format = optionalFormat.get();
                    movieDetails.setFormat(format);

                    Optional<Publisher> optionalPublisher = publisherRepository.findById(publisherId);
                    if (optionalPublisher.isPresent()) {
                        Publisher publisher = optionalPublisher.get();
                        movieDetails.setPublisher(publisher);

                        Optional<Country> optionalCoutry = countryRepository.findById(countryId);
                        if (optionalCoutry.isPresent()) {
                            Country country = optionalCoutry.get();
                            movieDetails.setCountry(country);
                            movieDetails.setMovie(movie);
                            movieDetailsRepository.save(movieDetails);
                            actionLogService.logging("Добавление информации фильма \"" + movie.getMovieName() + "\" " +
                                    "(ID: " + movie.getMovieId() + ") " +
                                    "с параметрами " + format.getName() + "/" + publisher.getName() + "/" + country.getName() + "/" + movieDetails.getDuration());
                        }
                    }
                }
            }
        }

        return "redirect:/movieDetails?movieId="+movieId;
    }

    @GetMapping("/movieDetails/deleteDetail")
    public String deleteMovieDetails(HttpServletRequest request,
                                          @RequestParam Long movieId,
                                          @RequestParam Long detailId) {
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        Movie movie = new Movie();
        if(optionalMovie.isPresent()) {
            movie = optionalMovie.get();

            if(movie.getCreatedBy().equals(userService.getCurUserId()) || userService.getCurUserRole().equals("ADMIN")) {
                Optional<MovieDetails> optionalMovieDetails = movieDetailsRepository.findById(detailId);
                if (optionalMovieDetails.isPresent()) {
                    MovieDetails movieDetails = optionalMovieDetails.get();

                    actionLogService.logging("Удаление информации фильма \"" + movie.getMovieName() + "\" " +
                            "(ID: " + movie.getMovieId() + ") " +
                            "с параметрами " + movieDetails.getFormat().getName()+ "/" + movieDetails.getPublisher().getName() + "/" + movieDetails.getCountry().getName() + "/" + movieDetails.getDuration());
                    movieDetailsRepository.deleteById(detailId);
                }
            }
        }
        return "redirect:/movieDetails?movieId=" + movieId;
    }
}