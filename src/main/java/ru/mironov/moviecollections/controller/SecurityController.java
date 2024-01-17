package ru.mironov.moviecollections.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import ru.mironov.moviecollections.dto.UserDto;
import ru.mironov.moviecollections.entity.User;
import ru.mironov.moviecollections.repository.RoleRepository;
import ru.mironov.moviecollections.repository.UserRepository;
import ru.mironov.moviecollections.service.ActionLogService;
import ru.mironov.moviecollections.service.MoviesStatistic;
import ru.mironov.moviecollections.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Controller
public class SecurityController {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    private UserService userService;
    private ActionLogService actionLogService;
    private MoviesStatistic moviesStatistic;
    public SecurityController(UserService userService, ActionLogService actionLogService, MoviesStatistic moviesStatistic) {
        this.userService = userService;
        this.actionLogService = actionLogService;
        this.moviesStatistic = moviesStatistic;
    }
    @GetMapping("/index")
    public String home(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Главная");
        model.addAttribute("currentUrl", request.getRequestURI());
        return "index";
    }
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Войти в MovieCollections");
        model.addAttribute("currentUrl", request.getRequestURI());
        return "login";
    }
    @GetMapping("/login/success")
    public String loginSuc(){
        actionLogService.logging("Успешная авторизация");
        return "redirect:/movies";
    }
    @GetMapping("/logout")
    public String logoutSuc(){
        actionLogService.logging("Завершение сессии");
        return "redirect:/logout/success";
    }
    @GetMapping("/register")
    public String showRegistrationForm(Model model, HttpServletRequest request){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Зарегистрироваться в ");
        model.addAttribute("currentUrl", request.getRequestURI());
        return "register";
    }
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model){
        User existingUser = userService.findUserByUsername(userDto.getUsername());
        if(existingUser != null && existingUser.getUsername() != null && !existingUser.getUsername().isEmpty()){
            result.rejectValue("username", null,
                    "Уже зарегистрирована учётная запись с указанным Именем пользователя.");
        }
        if(result.hasErrors()){
            model.addAttribute("user", userDto);
            return "/register";
        }
        userService.saveUser(userDto);
        actionLogService.logging(userService.findUserByUsername(userDto.getUsername()).getId(), "Регистрация");
        return "redirect:/register?success";
    }
    @GetMapping("/users")
    public String users(Model model, HttpServletRequest request){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Список пользователей");
        return "list-users";
    }
    @GetMapping("/updateUser")
    public ModelAndView updateUser(@RequestParam Long userId) {
        ModelAndView mav = new ModelAndView("update-user-form");
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = new User();
        if(optionalUser.isPresent()){
            user = optionalUser.get();
        }
        Long roleId = user.getRoles().get(0).getId();
        mav.addObject("user", user);
        mav.addObject("userRole", roleId);
        mav.addObject("roles", roleRepository.findAll());
        return mav;
    }
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, @RequestParam("userRole") Long userRole) {
        userService.addRoleToUser(user.getId(), userRole);
        actionLogService.logging("Смена роли пользователя \"" + userRepository.findById(user.getId()).get().getUsername() +
                "\" (ID: " + user.getId() + ") на " + roleRepository.findById(userRole).get().getName());
        return "redirect:/users";
    }
    @GetMapping("/roles")
    public ModelAndView roles(Model model){
        ModelAndView mav = new ModelAndView("list-roles");
        mav.addObject("roles", roleRepository.findAll());
        model.addAttribute("pageTitle", "Фильмы");
        return mav;
    }
    @GetMapping("/about")
    public String about(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "О приложении");
        model.addAttribute("currentUrl", request.getRequestURI());
        return "about";
    }
    @GetMapping("/statistic")
    public String statistic(Model model,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if(inputFlashMap != null) {
            String statistic = (String) inputFlashMap.get("statistic");
            model.addAttribute("statistic", statistic);
        }
        model.addAttribute("pageTitle", "Статистика");
        return "statistic-calculate";
    }
    @PostMapping("/statistic/calculate")
    public String statisticCalculate(HttpServletRequest request,
                                     @RequestParam("statOption") String statOption,
                                     RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("statistic", moviesStatistic.getStatistic(statOption));
        actionLogService.logging("Запрос статистики категории \"" + statOption + "\"");
        return "redirect:/statistic";
    }
}
