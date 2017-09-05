package com.bsmwireless.screens.editlogheader.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.editlogheader.EditLogHeaderActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {EditLogHeaderModule.class})
public interface EditLogHeaderComponent {
    void inject(EditLogHeaderActivity editLogHeaderActivity);
}
