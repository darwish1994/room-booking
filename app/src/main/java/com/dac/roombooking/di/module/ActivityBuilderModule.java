package com.dac.roombooking.di.module;

import com.dac.roombooking.view.AddWorkSpace;
import com.dac.roombooking.view.RoomDetailsActivity;
import com.dac.roombooking.view.WorkSpaceActivity;
import com.dac.roombooking.view.WorkSpacesActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilderModule {
    @ContributesAndroidInjector
    abstract AddWorkSpace ContributeAddWorkSpace();

    @ContributesAndroidInjector
    abstract WorkSpacesActivity ContributeWorkSpacesActivity();

    @ContributesAndroidInjector
    abstract WorkSpaceActivity ContributeWorkSpaceActivity();

    @ContributesAndroidInjector
    abstract RoomDetailsActivity ContributeRoomDetailsActivity();

}
