package com.bsmwireless.screens.editevent.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.editevent.EditEventActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {EditEventModule.class})
public interface EditEventComponent {
    void inject(EditEventActivity editEventActivity);
}
