package ru.kata.spring.boot_security.demo.servises;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.User;

@Service
public class UserService implements UserDetailsService {


    private final UserDao userDao;
    private final RoleDao roleDao;

    @Autowired
    public UserService(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
    }
    @Transactional(readOnly = true)
    public List<User> listUsers() {

        return userDao.index();

    }
    @Transactional(readOnly = true)
    public User getUser(Long id) {

        return userDao.show(id);
    }

    @Transactional
    public void saveUser(User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userDao.save(user);
    }

    @Transactional
    public void removeUser(Long id) {

        userDao.delete(id);

    }
    @Transactional
    public void update(Long id, User user) {;
        userDao.update(id, user);

    }

    @Transactional
    public User findByUsername(String name) {
        return listUsers().stream().filter(user -> user.getUsername().equals(name)).findAny()
                .orElse(null);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        System.out.println(user.getRoles());
        return user;
    }

}