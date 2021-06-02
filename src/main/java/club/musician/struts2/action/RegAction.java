package club.musician.struts2.action;

import club.musician.struts2.entity.User;
import club.musician.struts2.service.UserService;
import com.opensymphony.xwork2.Action;

public class RegAction implements Action {

    @Override
    public String execute() throws Exception {
        userService.register(user);
        return Action.SUCCESS;
    }


    private UserService userService;
    private User user;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
