package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.user.*;
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
    private final Map<Integer, User> userMap = new HashMap<>();
    private final Set<String> registeredEmails = new HashSet<>();
    private final Set<String> registeredLogins = new HashSet<>();
    private int userLastId = 1;
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
        int newUserId = getLastId();

        user.setId(newUserId);
        if(user.getName()==null||user.getName().isEmpty()){
            user.setName(user.getLogin());
        }
        registeredEmails.add(user.getEmail());
        registeredLogins.add(user.getLogin());
        userMap.put(newUserId,user);
        return user;
    }

    private synchronized int getLastId(){
        return userLastId++;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user){
        int userId = user.getId();
        if(!userMap.containsKey(userId))throw new UserIdNotExists(userId);
        checker(user, true);

        User oldUser = userMap.get(userId);

        if(!oldUser.getLogin().equals(user.getLogin())){
            registeredLogins.remove(oldUser.getLogin());
            registeredLogins.add(user.getLogin());
        }
        if(!oldUser.getEmail().equals(user.getEmail())){
            registeredEmails.remove(oldUser.getEmail());
            registeredEmails.add(user.getEmail());
        }

        oldUser.setName(user.getName());
        oldUser.setLogin(user.getLogin());
        oldUser.setBirthday(user.getBirthday());
        oldUser.setEmail(user.getEmail());

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
}
