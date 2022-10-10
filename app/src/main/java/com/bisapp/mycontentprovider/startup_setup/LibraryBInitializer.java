package com.bisapp.mycontentprovider.startup_setup;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.startup.Initializer;

import java.util.Collections;
import java.util.List;

public class LibraryBInitializer implements Initializer<LibraryB> {
    @NonNull
    @Override
    public LibraryB create(@NonNull Context context) {
        //place your logic for the initialization of Library B here
        return new LibraryB();
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return Collections.emptyList();
    }
}
