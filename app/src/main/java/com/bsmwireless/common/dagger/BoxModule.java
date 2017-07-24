package com.bsmwireless.common.dagger;

import com.bsmwireless.common.Constants;
import com.bsmwireless.data.network.Connection.ConnectionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by hsudhagar on 2017-07-17.
 */
@Module
public class BoxModule {


    @Singleton
    @Provides
   ConnectionManager provideConnectionManager()
   {
       return new ConnectionManager();
   }

}
