package ru.mironov.moviecollections.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import ru.mironov.moviecollections.entity.*;
import ru.mironov.moviecollections.repository.*;
import ru.mironov.moviecollections.service.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
public class InfoController {
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private FormatRepository formatRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ActionLogService actionLogService;
    @Autowired
    private MovieDetailsRepository movieDetailsRepository;

    @GetMapping("/infoes")
    public ModelAndView getInfoes(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("infoes");
        mav.addObject("pageTitle", "Информация");
        return mav;
    }

    @GetMapping("/infoes/genres")
    public ModelAndView getAllGenres(HttpServletRequest request){
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        ModelAndView mav = new ModelAndView("list-genres");

        if (inputFlashMap != null) {
            String error = "";

            Genre genre = (Genre) inputFlashMap.get("genreDeleteError");
            if(genre != null) {
                error = "Невозможно удалить жанр \"" + genre.getName() + "\", так как есть связанные с ним фильмы!";
            } else {
                genre = (Genre) inputFlashMap.get("genreMatchError");
                if(genre != null) {
                    error = "Операция отклонена. Жанр с именем \"" + genre.getName() + "\" уже существует.";
                }
            }

            if(!error.isEmpty()) mav.addObject("error", error);
        }

        mav.addObject("genres", genreRepository.findAll());
        mav.addObject("pageTitle", "Информация жанров");
        return mav;
    }

    @GetMapping("/infoes/genres/addGenreForm")
    public ModelAndView addGenreForm(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("add-genre-form");
        Genre genre = new Genre();
        mav.addObject("genre", genre);
        mav.addObject("pageTitle", "Добавить жанр");
        return mav;
    }

    @PostMapping("/infoes/genres/saveGenre")
    public String saveGenre(@ModelAttribute Genre genre, RedirectAttributes redirectAttributes) {
        Genre check = genreRepository.findByName(genre.getName());
        if(check == null) {
            genreRepository.save(genre);
            actionLogService.logging("Сохранение жанра \"" + genre.getName() + "\" (ID: " + genre.getId() + ")");
        } else {
            redirectAttributes.addFlashAttribute("genreMatchError", check);
        }

        return "redirect:/infoes/genres";
    }

    @GetMapping("/infoes/genres/updateGenre")
    public ModelAndView updateGenre(@RequestParam Long genreId) {
        ModelAndView mav = new ModelAndView("add-genre-form");
        Optional<Genre> optionalGenre = genreRepository.findById(genreId);
        Genre genre = new Genre();
        if(optionalGenre.isPresent()){
            genre = optionalGenre.get();
        }
        mav.addObject("genre", genre);
        mav.addObject("pageTitle", "Изменить жанр");
        return mav;
    }

    @GetMapping("/infoes/genres/deleteGenre")
    public String deleteGenre(@RequestParam Long genreId, RedirectAttributes redirectAttributes){
        Optional<Genre> optionalGenre = genreRepository.findById(genreId);
        Genre genre = new Genre();
        if(optionalGenre.isPresent()){
            genre = optionalGenre.get();

            List<Movie> movies = genre.getMovies();

            if(movies.size() == 0) {
                actionLogService.logging("Удаление жанра \"" + genre.getName() + "\" (ID: " + genre.getId() + ")");
                genreRepository.deleteById(genreId);
            } else {
                redirectAttributes.addFlashAttribute("genreDeleteError", genre);
            }
        }
        return "redirect:/infoes/genres";
    }

    @GetMapping("/infoes/formats")
    public ModelAndView getAllFormats(HttpServletRequest request){
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        ModelAndView mav = new ModelAndView("list-formats");

        if (inputFlashMap != null) {
            String error = "";

            Format format = (Format) inputFlashMap.get("formatDeleteError");
            if(format != null) {
                error = "Невозможно удалить формат \"" + format.getName() + "\", так как есть связанные с ним фильмы!";
            } else {
                format = (Format) inputFlashMap.get("formatMatchError");
                if(format != null) {
                    error = "Операция отклонена. Формат с именем \"" + format.getName() + "\" уже существует.";
                }
            }

            if(!error.isEmpty()) mav.addObject("error", error);
        }

        mav.addObject("formats", formatRepository.findAll());
        mav.addObject("pageTitle", "Информация форматов");
        return mav;
    }

    @GetMapping("/infoes/formats/addFormatForm")
    public ModelAndView addFormatForm(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("add-format-form");
        Format format = new Format();
        mav.addObject("format", format);
        mav.addObject("pageTitle", "Добавить формат");
        return mav;
    }

    @PostMapping("/infoes/formats/saveFormat")
    public String saveFormat(@ModelAttribute Format format, RedirectAttributes redirectAttributes) {
        Format check = formatRepository.findByName(format.getName());
        if(check == null) {
            formatRepository.save(format);
            actionLogService.logging("Сохранение формата \"" + format.getName() + "\" (ID: " + format.getId() + ")");
        } else {
            redirectAttributes.addFlashAttribute("formatMatchError", check);
        }

        return "redirect:/infoes/formats";
    }

    @GetMapping("/infoes/formats/updateFormat")
    public ModelAndView updateFormat(@RequestParam Long id) {
        ModelAndView mav = new ModelAndView("add-format-form");
        Optional<Format> optionalFormat = formatRepository.findById(id);
        Format format = new Format();
        if(optionalFormat.isPresent()){
            format = optionalFormat.get();
        }
        mav.addObject("format", format);
        mav.addObject("pageTitle", "Изменить формат");
        return mav;
    }

    @GetMapping("/infoes/formats/deleteFormat")
    public String deleteFormat(@RequestParam Long id, RedirectAttributes redirectAttributes){
        Optional<Format> optionalFormat = formatRepository.findById(id);
        Format format;
        if(optionalFormat.isPresent()){
            format = optionalFormat.get();

            List<MovieDetails> movieDetails = movieDetailsRepository.findAllByFormat(format);

            if(movieDetails.size() == 0) {
                actionLogService.logging("Удаление формата \"" + format.getName() + "\" (ID: " + format.getId() + ")");
                formatRepository.deleteById(id);
            } else {
                redirectAttributes.addFlashAttribute("formatDeleteError", format);
            }
        }
        return "redirect:/infoes/formats";
    }
    @GetMapping("/infoes/publishers")
    public ModelAndView getAllPublishers(HttpServletRequest request){
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        ModelAndView mav = new ModelAndView("list-publishers");

        if (inputFlashMap != null) {
            String error = "";

            Publisher publisher = (Publisher) inputFlashMap.get("publisherDeleteError");
            if(publisher != null) {
                error = "Невозможно удалить издательство \"" + publisher.getName() + "\", так как есть связанные с ним фильмы!";
            } else {
                publisher = (Publisher) inputFlashMap.get("publisherMatchError");
                if(publisher != null) {
                    error = "Операция отклонена. Издательство с именем \"" + publisher.getName() + "\" уже существует.";
                }
            }

            if(!error.isEmpty()) mav.addObject("error", error);
        }

        mav.addObject("publishers", publisherRepository.findAll());
        mav.addObject("pageTitle", "Наименование издательств");
        return mav;
    }

    @GetMapping("/infoes/publishers/addPublisherForm")
    public ModelAndView addPublisherForm(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("add-publisher-form");
        Publisher publisher = new Publisher();
        mav.addObject("publisher", publisher);
        mav.addObject("pageTitle", "Добавить издательство");
        return mav;
    }

    @PostMapping("/infoes/publishers/savePublisher")
    public String savePublisher(@ModelAttribute Publisher publisher, RedirectAttributes redirectAttributes) {
        Publisher check = publisherRepository.findByName(publisher.getName());
        if(check == null) {
            publisherRepository.save(publisher);
            actionLogService.logging("Сохранение издательства \"" + publisher.getName() + "\" (ID: " + publisher.getId() + ")");
        } else {
            redirectAttributes.addFlashAttribute("publisherMatchError", check);
        }

        return "redirect:/infoes/publishers";
    }

    @GetMapping("/infoes/publishers/updatePublisher")
    public ModelAndView updatePublisher(@RequestParam Long id) {
        ModelAndView mav = new ModelAndView("add-publisher-form");
        Optional<Publisher> optionalPublisher = publisherRepository.findById(id);
        Publisher publisher = new Publisher();
        if(optionalPublisher.isPresent()){
            publisher = optionalPublisher.get();
        }
        mav.addObject("publisher", publisher);
        mav.addObject("pageTitle", "Изменить издательство");
        return mav;
    }

    @GetMapping("/infoes/publishers/deletePublisher")
    public String deletePublisher(@RequestParam Long id, RedirectAttributes redirectAttributes){
        Optional<Publisher> optionalPublisher = publisherRepository.findById(id);
        Publisher publisher ;
        if(optionalPublisher.isPresent()){
            publisher = optionalPublisher.get();

            List<MovieDetails> movieDetails = movieDetailsRepository.findAllByPublisher(publisher);

            if(movieDetails.size() == 0) {
                actionLogService.logging("Удаление издательства \"" + publisher.getName() + "\" (ID: " + publisher.getId() + ")");
                publisherRepository.deleteById(id);
            } else {
                redirectAttributes.addFlashAttribute("publisherDeleteError", publisher);
            }
        }
        return "redirect:/infoes/publishers";
    }
    @GetMapping("/infoes/countrys")
    public ModelAndView getAllCountrys(HttpServletRequest request){
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        ModelAndView mav = new ModelAndView("list-countrys");

        if (inputFlashMap != null) {
            String error = "";

            Country country = (Country) inputFlashMap.get("countryDeleteError");
            if(country != null) {
                error = "Невозможно удалить страну \"" + country.getName() + "\", так как есть связанные с ним фильмы!";
            } else {
                country = (Country) inputFlashMap.get("countryMatchError");
                if(country != null) {
                    error = "Операция отклонена. Страна с именем \"" + country.getName() + "\" уже существует.";
                }
            }

            if(!error.isEmpty()) mav.addObject("error", error);
        }

        mav.addObject("countrys", countryRepository.findAll());
        mav.addObject("pageTitle", "Наименование стран");
        return mav;
    }

    @GetMapping("/infoes/countrys/addCountryForm")
    public ModelAndView addCountryForm(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("add-country-form");
        Country country = new Country();
        mav.addObject("country", country);
        mav.addObject("pageTitle", "Добавить страну");
        return mav;
    }

    @PostMapping("/infoes/countrys/saveCountry")
    public String saveCountry(@ModelAttribute Country country, RedirectAttributes redirectAttributes) {
        Country check = countryRepository.findByName(country.getName());
        if(check == null) {
            countryRepository.save(country);
            actionLogService.logging("Сохранение страны \"" + country.getName() + "\" (ID: " + country.getId() + ")");
        } else {
            redirectAttributes.addFlashAttribute("countryMatchError", check);
        }

        return "redirect:/infoes/countrys";
    }

    @GetMapping("/infoes/countrys/updateCountry")
    public ModelAndView updateCountry(@RequestParam Long id) {
        ModelAndView mav = new ModelAndView("add-country-form");
        Optional<Country> optionalCountry = countryRepository.findById(id);
        Country country = new Country();
        if(optionalCountry.isPresent()){
            country = optionalCountry.get();
        }
        mav.addObject("country", country);
        mav.addObject("pageTitle", "Изменить страну");
        return mav;
    }

    @GetMapping("/infoes/countrys/deleteCountry")
    public String deleteCountry(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Optional<Country> optionalCountry = countryRepository.findById(id);
        Country country;
        if (optionalCountry.isPresent()) {
            country = optionalCountry.get();

            List<MovieDetails> movieDetails = movieDetailsRepository.findAllByCountry(country);

            if (movieDetails.size() == 0) {
                actionLogService.logging("Удаление страны \"" + country.getName() + "\" (ID: " + country.getId() + ")");
                countryRepository.deleteById(id);
            } else {
                redirectAttributes.addFlashAttribute("countryDeleteError", country);
            }
        }
        return "redirect:/infoes/countrys";
    }
}

