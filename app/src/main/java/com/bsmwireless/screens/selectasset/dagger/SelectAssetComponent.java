package com.bsmwireless.screens.selectasset.dagger;


import com.bsmwireless.common.dagger.AppComponent;
import com.bsmwireless.screens.selectasset.SelectAssetActivity;

import dagger.Component;

@SelectAssetScope
@Component(dependencies = {AppComponent.class}, modules = {SelectAssetModule.class})
public interface SelectAssetComponent {
    void inject(SelectAssetActivity selectAssetActivity);
}
