package ru.mironov.moviecollections.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.mironov.moviecollections.entity.ActionLog;
import ru.mironov.moviecollections.repository.ActionLogRepository;
import ru.mironov.moviecollections.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
public class ActionLogController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActionLogRepository actionLogRepository;
    @GetMapping("/logs")
    public ModelAndView getAllLogs(HttpServletRequest request){
        Pageable pageable = PageRequest.of(0, 50, Sort.by("timestamp").descending());
        Page<ActionLog> logsPage = actionLogRepository.findAll(pageable);
        List<ActionLog> logs = logsPage.toList();

        ModelAndView mav = new ModelAndView("list-logs");
        mav.addObject("logs", logs);
        mav.addObject("userrep", userRepository);
        mav.addObject("pageTitle", "Логи действий пользователей");
        return mav;
    }
}
