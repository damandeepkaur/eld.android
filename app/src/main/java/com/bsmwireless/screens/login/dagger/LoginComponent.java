package com.bsmwireless.screens.login.dagger;


import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.login.LoginActivity;

import dagger.Component;

@LoginScope
@Component(dependencies = {AppComponent.class}, modules = {LoginModule.class})
public interface LoginComponent {
    void inject(LoginActivity loginActivity);
}
