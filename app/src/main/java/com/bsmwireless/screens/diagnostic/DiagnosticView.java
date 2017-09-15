package com.bsmwireless.screens.diagnostic;

import com.bsmwireless.models.ELDEvent;

import java.util.List;

public interface DiagnosticView {
    void showNoEvents();

    void showEvents(List<ELDEvent> events, String timezone);
}
