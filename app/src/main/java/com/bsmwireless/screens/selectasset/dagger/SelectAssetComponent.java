package com.bsmwireless.screens.selectasset.dagger;


import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class}, modules = {SelectAssetModule.class})
public interface SelectAssetComponent {
    void inject(SelectAssetActivity selectAssetActivity);
}
