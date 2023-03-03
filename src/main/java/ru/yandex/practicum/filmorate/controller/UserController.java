package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Stanislav Makarov
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    Map<Integer, User> userMap = new HashMap<>();
    Set<String> registeredEmails = new HashSet<>();
    Set<String> registeredLogins = new HashSet<>();
    @GetMapping
    public List<User> getAllUsers(){
        return List.copyOf(userMap.values());
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") String id){
        int userId;
        try {
            userId = Integer.parseInt(id);
        }catch (NumberFormatException e){
            throw new UserIdNotExists(id);
        }

        if(!userMap.containsKey(userId))throw new UserIdNotExists(userId);
        return userMap.get(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user){
        checker(user, false);
        int newUserId = userMap.size()+1;
        String email = user.getEmail().toLowerCase();
        String login = user.getLogin().toLowerCase();
        User newUser = new User(newUserId, login);
        newUser.setBirthday(user.getBirthday());
        newUser.setEmail(email);
        if(user.getName()==null||user.getName().isEmpty()){
            newUser.setName(user.getLogin());
        } else {
            newUser.setName(user.getName());
        }
        registeredEmails.add(email);
        registeredLogins.add(login);
        userMap.put(newUserId,newUser);
        return newUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user){
        int userId = user.getId();
        if(!userMap.containsKey(userId))throw new UserIdNotExists(userId);
        checker(user, true);

        String email = user.getEmail().toLowerCase();
        String login = user.getLogin().toLowerCase();

        User oldUser = userMap.get(userId);

        //if(!oldUser.getLogin().equals(login))throw new LoginIsImmutable();
        oldUser.setName(user.getName());
        oldUser.setLogin(user.getLogin());
        oldUser.setBirthday(user.getBirthday());
        oldUser.setEmail(email);
        registeredEmails.add(email);
        userMap.put(userId,oldUser);
        return oldUser;
    }

    public void checker(User user, boolean updateFlag){
        String email = user.getEmail().toLowerCase();
        String login = user.getLogin().toLowerCase();
        if(!updateFlag){
            //+email уже существует
            if(registeredEmails.contains(email))throw new EmailAlreadyRegistered(email);
            //+логин уже занят
            if(registeredLogins.contains(login))throw new LoginAlreadyRegistered(login);
        }
        if(email.isEmpty())throw new EmailWrong(email, "электронная почта не может быть пустой");
        if(!email.contains("@"))throw new EmailWrong(email, "электронная почта должна содержать символ @");
        if(email.contains(" "))throw new EmailWrong(email, "электронная почта не должна содержать пробелы");
        if(login.isEmpty())throw new LoginWrong(login, "логин не может быть пустым");
        if(login.contains(" "))throw new LoginWrong(login, "логин не может содержать пробелы");
        if(!user.getBirthday().isBefore(LocalDate.now())) throw new BirthDayDateWrong();
    }

    private static class UserIdNotExists extends RuntimeException {
        public UserIdNotExists(int id){
            log.error(String.format("Пользователь с ИД %d не найден в системе", id));
        }
        public UserIdNotExists(String id){
            log.error(String.format("Пользователь с ИД %s не найден в системе", id));
        }
    }

    private static class LoginIsImmutable extends RuntimeException {
        public LoginIsImmutable(){
            log.error("Логин пользователя не изменяется");
        }
    }
    private static class EmailAlreadyRegistered extends RuntimeException {
        public EmailAlreadyRegistered(String email){
            log.error(String.format("EMail %s уже зарегистрирован", email));
        }
    }

    private static class EmailWrong extends RuntimeException {
        public EmailWrong(String email, String errorText){
            log.error(String.format("EMail %s не корректен: %s", email, errorText));
        }
    }

    private static class LoginAlreadyRegistered extends RuntimeException {
        public LoginAlreadyRegistered(String login){
            log.error(String.format("Логин %s уже зарегистрирован", login));
        }
    }

    private static class LoginWrong extends RuntimeException {
        public LoginWrong(String login, String errorText){
            log.error(String.format("Логин \"%s\" не корректен: %s", login, errorText));
        }
    }

    private static class BirthDayDateWrong extends RuntimeException {
        public BirthDayDateWrong(){
            log.error("дата рождения не может быть в будущем");
        }
    }
}
