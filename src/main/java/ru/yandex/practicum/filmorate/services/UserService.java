package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.user.FriendAddError;
import ru.yandex.practicum.filmorate.exceptions.user.FriendRemoveError;

import java.util.*;

/**
 * @author Stanislav Makarov
 */
@Service
@Slf4j
public class UserService {
    private final Map<Integer, MyFriends> friendsMap = new HashMap<>();

    public List<Integer> getFriends(int userId){
        if(!friendsMap.containsKey(userId))return new ArrayList<>();
        return List.copyOf(friendsMap.get(userId).getFriendsSet());
    }

    public void makeFriendship(int userId1, int userId2) {
        if(userId1==userId2){
            //log.info("Нельзя добавлять самого себя в друзья");
            throw new FriendAddError("Нельзя добавлять самого себя в друзья");
        }
        addFriend(userId1, userId2);
        addFriend(userId2, userId1);
    }

    public void removeFriendship(int userId1, int userId2){
       removeFriend(userId1, userId2);
       removeFriend(userId2, userId1);
    }

    public boolean checkFriendship(int userId1, int userId2){
        if(!friendsMap.containsKey(userId1)||!friendsMap.containsKey(userId2)){
            return false;
        }
        return friendsMap.get(userId1).getFriendsSet().contains(userId2);
    }

    public List<Integer> getOursFriendList(int userId1, int userId2){
        if(!friendsMap.containsKey(userId1)||!friendsMap.containsKey(userId2)) return new ArrayList<>();
        Set<Integer> friendSet1 = friendsMap.get(userId1).getFriendsSet();
        Set<Integer> friendSet2 = friendsMap.get(userId2).getFriendsSet();
        Set<Integer> friends = intersection(friendSet1, friendSet2);
        return List.copyOf(friends);
    }

    private static Set<Integer> intersection(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> result = new HashSet<>(set1);
        result.retainAll(set2);
        return result;
    }

    private void addFriend(int userId, int friendId) {
        MyFriends myFriends;
        if (friendsMap.containsKey(userId)){
            myFriends = friendsMap.get(userId);
            if(myFriends.isMyFriend(friendId)) {
                throw new FriendAddError("дружба уже установлена");
            }
        }
        else myFriends = new MyFriends(userId);
        myFriends.put(friendId);
        friendsMap.put(userId,myFriends);
    }

    private void removeFriend(int userId, int friendId){
        MyFriends myFriends;
        if (friendsMap.containsKey(userId)){
            myFriends = friendsMap.get(userId);
            if(!myFriends.isMyFriend(friendId)) throw new FriendRemoveError();
            myFriends.remove(friendId);
        } else throw new FriendRemoveError();
    }

    static class MyFriends {
        int userId;
        private final Set<Integer> friends = new HashSet<>();
        public MyFriends(int userId) {
            this.userId = userId;
        }

        public Set<Integer> getFriendsSet(){
            return Set.copyOf(friends);
        }

        void put(int frindId) {
            if (frindId > 0) friends.add(frindId);
        }

        boolean isMyFriend(int friendId) {
            return friends.contains(friendId);
        }

        void remove(int friendId) {
            friends.remove(friendId);
        }
    }
}
