package ru.mironov.moviecollections.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mironov.moviecollections.dto.UserDto;
import ru.mironov.moviecollections.entity.Role;
import ru.mironov.moviecollections.entity.User;
import ru.mironov.moviecollections.repository.RoleRepository;
import ru.mironov.moviecollections.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;

        initRoles();
    }

    @Override
    public void saveUser(UserDto userDto){
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role;
        if(userRepository.count() == 0) role = roleRepository.findByName("ADMIN");
            else role = roleRepository.findByName("READONLY");

        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String username) { return userRepository.findByUsername(username); }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setRole(user.getRoles().get(0).getCaption());
        return userDto;
    }

    private void initRoles(){
        String roleName = "";
        String roleCaption = "";

        for(int i=0;i<3;i++){
            switch(i){
                case 0: roleName = "ADMIN"; roleCaption = "Администратор сайта"; break;
                case 1: roleName = "USER"; roleCaption = "Простой пользователь"; break;
                case 2: roleName = "READONLY"; roleCaption = "Пользователь с правами \"Только для чтения\"";
            }

            if(!checkRoleExist(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                role.setCaption(roleCaption);
                roleRepository.save(role);
            }
        }
    }

    private boolean checkRoleExist(String roleName){
        boolean ret = false;

        Role role = roleRepository.findByName(roleName);
        if(role != null) ret = true;

        return ret;
    }

    @Override
    public void addRoleToUser(Long userId, Long roleId) {
        Optional<User> userO = userRepository.findById(userId);
        if (userO.isPresent()) {
            Optional<Role> roleO = roleRepository.findById(roleId);
            if (roleO.isPresent()) {
                User user = userO.get();
                Role role = roleO.get();
                user.getRoles().clear();
                user.getRoles().add(role);
                userRepository.save(user);
            }
        }
    }

    @Override
    public Long getCurUserId(){
        Long ret = 0L;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        User user = userRepository.findByUsername(currentUserName);
        if(user != null) ret = user.getId();

        return ret;
    }

    public String getCurUserRole(){
        String ret = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ret = authentication.getAuthorities().toString().replace("[", "").replace("]", "");
        return ret;
    }
}
