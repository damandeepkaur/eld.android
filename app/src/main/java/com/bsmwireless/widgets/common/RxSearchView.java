package com.bsmwireless.widgets.common;

import android.support.v7.widget.SearchView;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.BehaviorSubject;

public class RxSearchView {

    public static Observable<String> queryTextChanges(@NonNull final SearchView searchView) {
        final BehaviorSubject<String> subject = BehaviorSubject.create();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                subject.onComplete();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                subject.onNext(newText);
                return true;
            }
        });

        return subject;
    }
}
