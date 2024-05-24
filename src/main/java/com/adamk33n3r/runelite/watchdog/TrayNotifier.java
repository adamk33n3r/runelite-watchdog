package com.adamk33n3r.runelite.watchdog;

import net.runelite.client.RuneLite;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.util.OSType;

import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.TrayIcon;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This file is as-much-as-possible a copy of {@link net.runelite.client.Notifier}
 * The copied functions can be run through a differ on Notifier's versions of the functions
 */
@Slf4j
@Singleton
public class TrayNotifier {
    // Copied from RuneLite's Notifier class
    private final RuneLiteConfig runeLiteConfig;
    private final ClientUI clientUI;
    private final ScheduledExecutorService executorService;
    private final String appName;
    private final Path notifyIconPath;
    private transient boolean terminalNotifierAvailable;
    private static final String DOUBLE_QUOTE = "\"";
    private static final Escaper SHELL_ESCAPE = Escapers.builder()
            .addEscape('"', "'")
            .build();

    @Inject
    public TrayNotifier(
        final ClientUI clientUI,
        final RuneLiteConfig runeliteConfig,
        final ScheduledExecutorService executorService,
        final ChatMessageManager chatMessageManager,
        final EventBus eventBus,
        @Named("runelite.title") final String appName
    ) {
        this.clientUI = clientUI;
        this.runeLiteConfig = runeliteConfig;
        this.executorService = executorService;
        this.appName = appName;
        this.notifyIconPath = RuneLite.RUNELITE_DIR.toPath().resolve("icon.png");

        // Check if we are running in the launcher because terminal-notifier notifications don't work
        // if the group/sender are unknown to it.
        if (!Strings.isNullOrEmpty(RuneLiteProperties.getLauncherVersion()) && OSType.getOSType() == OSType.MacOS)
        {
            executorService.execute(() -> {
                terminalNotifierAvailable = isTerminalNotifierAvailable();
            });
        }
    }

    public void notify(
            final String title,
            final String message,
            final TrayIcon.MessageType type)
    {
        this.sendNotification(title, message, type);
    }

    // Copy of RuneLite's Notifier::sendNotification
    private void sendNotification(
            final String title,
            final String message,
            final TrayIcon.MessageType type)
    {
        final String escapedTitle = SHELL_ESCAPE.escape(title);
        final String escapedMessage = SHELL_ESCAPE.escape(message);

        switch (OSType.getOSType())
        {
            case Linux:
                sendLinuxNotification(escapedTitle, escapedMessage, type);
                break;
            case MacOS:
                sendMacNotification(escapedTitle, escapedMessage);
                break;
            default:
                sendTrayNotification(title, message, type);
        }
    }

    // Copy of RuneLite's Notifier::sendTrayNotification
    private void sendTrayNotification(
            final String title,
            final String message,
            final TrayIcon.MessageType type)
    {
        if (clientUI.getTrayIcon() != null)
        {
            clientUI.getTrayIcon().displayMessage(title, message, type);
        }
    }

    // Copy of RuneLite's Notifier::sendLinuxNotification
    private void sendLinuxNotification(
            final String title,
            final String message,
            final TrayIcon.MessageType type)
    {
        final List<String> commands = new ArrayList<>();
        commands.add("notify-send");
        commands.add(title);
        commands.add(message);
        commands.add("-a");
        commands.add(SHELL_ESCAPE.escape(appName));
        commands.add("-i");
        commands.add(SHELL_ESCAPE.escape(notifyIconPath.toAbsolutePath().toString()));
        commands.add("-u");
        commands.add(toUrgency(type));
        if (runeLiteConfig.notificationTimeout() > 0)
        {
            commands.add("-t");
            commands.add(String.valueOf(runeLiteConfig.notificationTimeout()));
        }

        executorService.submit(() ->
        {
            try
            {
                Process notificationProcess = sendCommand(commands);

                boolean exited = notificationProcess.waitFor(500, TimeUnit.MILLISECONDS);
                if (exited && notificationProcess.exitValue() == 0)
                {
                    return;
                }
            }
            catch (IOException | InterruptedException ex)
            {
                log.debug("error sending notification", ex);
            }

            // fall back to tray notification
            sendTrayNotification(title, message, type);
        });
    }

    // Copy of RuneLite's Notifier::sendMacNotification
    private void sendMacNotification(final String title, final String message)
    {
        final List<String> commands = new ArrayList<>();

        if (terminalNotifierAvailable)
        {
            Collections.addAll(commands,
                    "sh", "-lc", "\"$@\"", "--",
                    "terminal-notifier",
                    "-title", title,
                    "-message", message,
                    "-group", "net.runelite.launcher",
                    "-sender", "net.runelite.launcher"
            );
        }
        else
        {
            commands.add("osascript");
            commands.add("-e");

            final String script = "display notification " + DOUBLE_QUOTE +
                    message +
                    DOUBLE_QUOTE +
                    " with title " +
                    DOUBLE_QUOTE +
                    title +
                    DOUBLE_QUOTE;

            commands.add(script);
        }

        try
        {
            sendCommand(commands);
        }
        catch (IOException ex)
        {
            log.warn("error sending notification", ex);
        }
    }

    // Copy of RuneLite's Notifier::sendCommand
    private static Process sendCommand(final List<String> commands) throws IOException
    {
        return new ProcessBuilder(commands)
                .redirectErrorStream(true)
                .start();
    }

    // Copy of RuneLite's Notifier::isTerminalNotifierAvailable
    private boolean isTerminalNotifierAvailable()
    {
        try
        {
            // The PATH seen by Cocoa apps does not resemble that seen by the shell, so we defer to the latter.
            final Process exec = Runtime.getRuntime().exec(new String[]{"sh", "-lc", "terminal-notifier -help"});
            if (!exec.waitFor(2, TimeUnit.SECONDS))
            {
                return false;
            }
            return exec.exitValue() == 0;
        }
        catch (IOException | InterruptedException e)
        {
            return false;
        }
    }

    // Copy of RuneLite's Notifier::toUrgency
    private static String toUrgency(TrayIcon.MessageType type)
    {
        switch (type)
        {
            case WARNING:
            case ERROR:
                return "critical";
            default:
                return "normal";
        }
    }

}
