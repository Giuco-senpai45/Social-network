package controller.pages;

import main.domain.User;
import main.service.MasterService;

public class PageObject {

    private final MasterService service;

    private final User loggedUser;

    public PageObject(MasterService service, User loggedUser) {
        this.service = service;
        this.loggedUser = loggedUser;
    }


    public MasterService getService() {
        return service;
    }

    public User getLoggedUser() {
        return loggedUser;
    }
}
