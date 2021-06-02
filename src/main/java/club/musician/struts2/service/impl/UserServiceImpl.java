package club.musician.struts2.service.impl;

import club.musician.struts2.dao.UserDao;
import club.musician.struts2.entity.User;
import club.musician.struts2.service.UserService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public int register(User user) {
        System.out.println("UserServiceImpl.register....");
        return userDao.save(user);
    }
}
