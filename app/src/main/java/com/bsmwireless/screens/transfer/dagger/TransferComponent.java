package com.bsmwireless.screens.transfer.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.transfer.TransferActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {TransferModule.class})
public interface TransferComponent {
    void inject(TransferActivity transferActivity);
}
