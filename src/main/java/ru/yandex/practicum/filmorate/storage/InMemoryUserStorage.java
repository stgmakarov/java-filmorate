package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.user.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Stanislav Makarov
 */
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> userMap = new HashMap<>();
    private final Set<String> registeredEmails = new HashSet<>();
    private final Set<String> registeredLogins = new HashSet<>();
    private int userLastId = 1;

    private synchronized int getLastId(){
        return userLastId++;
    }

    @Override
    public User create(User user) {
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

    @Override
    public User update(User user) {
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

    @Override
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

    @Override
    public List<User> getListOfUsers() {
        return List.copyOf(userMap.values());
    }

    @Override
    public List<User> getListOfUsers(List<Integer> users) {
        return userMap.values().stream()
                .filter(user -> users.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(int userId) {
        if(!userMap.containsKey(userId))throw new UserIdNotExists(userId);
        return userMap.get(userId);
    }
}
