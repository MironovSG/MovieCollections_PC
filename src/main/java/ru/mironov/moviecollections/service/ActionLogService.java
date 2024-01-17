package ru.mironov.moviecollections.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.mironov.moviecollections.entity.ActionLog;
import ru.mironov.moviecollections.entity.User;
import ru.mironov.moviecollections.repository.ActionLogRepository;
import ru.mironov.moviecollections.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class ActionLogService {
    final private ActionLogRepository actionLogRepository;
    final private UserRepository userRepository;
    @Autowired
    public ActionLogService(ActionLogRepository actionLogRepository, UserRepository userRepository){
        this.actionLogRepository = actionLogRepository;
        this.userRepository = userRepository;
    }
    public List<ActionLog> getAllLog() { return actionLogRepository.findAll(); }
    public void logging(Long userId, String action){
        ActionLog actionLog = new ActionLog();
        actionLog.setUserId(userId);
        actionLog.setAction(action);
        actionLog.setTimestamp(LocalDateTime.now());
        actionLogRepository.save(actionLog);
    }
    public void logging(String action){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        User user = userRepository.findByUsername(currentUserName);
        this.logging(user.getId(), action);
    }
}
