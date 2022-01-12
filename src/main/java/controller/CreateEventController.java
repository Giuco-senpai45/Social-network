package controller;

import controller.pages.PageObject;
import main.domain.User;

public class CreateEventController {
    private PageObject pageObject;
    private User loggedUser;

    public void init(PageObject pageObject, User loggedUser) {
        this.pageObject = pageObject;
        this.loggedUser = loggedUser;
    }
}
