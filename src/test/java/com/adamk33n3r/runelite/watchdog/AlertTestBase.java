package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.ui.panels.HistoryPanel;
import com.google.inject.testing.fieldbinder.Bind;
import net.runelite.client.eventbus.EventBus;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.inject.Provider;

public abstract class AlertTestBase extends TestBase {
    @Mock
    @Bind
    EventBus eventBus;

    @Mock
    HistoryPanel historyPanel;
    @Mock
    Provider<HistoryPanel> historyPanelProvider;


    @Before
    public void before() throws NoSuchFieldException {
        super.before();

        Mockito.when(historyPanelProvider.get()).thenReturn(this.historyPanel);
    }
}
