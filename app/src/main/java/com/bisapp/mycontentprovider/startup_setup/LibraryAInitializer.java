package com.bisapp.mycontentprovider.startup_setup;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.startup.Initializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibraryAInitializer implements Initializer<LibraryA> {
    @NonNull
    @Override
    public LibraryA create(@NonNull Context context) {

        //place your logic for the initialization of Library A here
        return new LibraryA();
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        //if the library A depends on any library components
        //place them here in a list form.
        return new ArrayList<Class<? extends Initializer<?>>>(Collections.singleton(LibraryBInitializer.class));
    }
}
