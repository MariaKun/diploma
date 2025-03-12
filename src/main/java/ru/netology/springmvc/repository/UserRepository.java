package ru.netology.springmvc.repository;

import org.springframework.stereotype.Repository;
import ru.netology.springmvc.Authorities;
import ru.netology.springmvc.entity.Users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {

    public List<Authorities> getUserAuthorities(Users user) {
        ConcurrentHashMap<String, String> mapUsers = new ConcurrentHashMap<>();
        mapUsers.put("user1", "1234");
        mapUsers.put("user2", "4567");
        mapUsers.put("user3", "6789");

        ConcurrentHashMap<String, List<Authorities>> mapAuthorities = new ConcurrentHashMap<>();
        mapAuthorities.put("user1", Arrays.asList(Authorities.READ, Authorities.WRITE));
        mapAuthorities.put("user2", Arrays.asList(Authorities.READ));

        if (mapUsers.containsKey(user.getUserName())) {
            if (mapUsers.get(user.getUserName()).equals(user.getPassword())) {
                if (mapAuthorities.containsKey(user.getUserName())) {
                    return mapAuthorities.get(user.getUserName());
                }
            }
        }
        return new ArrayList<>();
    }
}
