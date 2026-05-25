package com.gym.view;

import com.gym.model.users.User;
import com.gym.repository.GymContext;

public interface IDisplayMenu {

    void displayMenu(GymContext context, User loggedInUser);
}
