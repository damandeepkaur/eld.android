package com.bsmwireless.screens.diagnostic.dagger;

import com.bsmwireless.common.dagger.ActivityScope;
import com.bsmwireless.screens.diagnostic.DiagnosticDialog;
import com.bsmwireless.screens.diagnostic.DiagnosticPresenter;
import com.bsmwireless.screens.diagnostic.DiagnosticView;
import com.bsmwireless.screens.diagnostic.MalfunctionDialog;

import dagger.BindsInstance;
import dagger.Subcomponent;

@Subcomponent
@ActivityScope
public interface DiagnosticComponent {

    void inject(DiagnosticDialog diagnosticDialog);
    void inject(MalfunctionDialog malfunctionDialog);

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance Builder dialogType(DiagnosticPresenter.EventType eventType);
        @BindsInstance Builder view(DiagnosticView diagnosticView);
        DiagnosticComponent build();
    }
}
