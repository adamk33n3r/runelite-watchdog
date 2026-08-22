package com.adamk33n3r.nodegraph.nodes;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.runelite.watchdog.notifications.MessageNotification;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import lombok.*;

import javax.annotation.Nullable;

@Getter
public class ActionNode extends Node {
    private final Notification notification;

    private final VarInput<Boolean> enabled = new VarInput<>(this, "Enabled", Boolean.class, true);
    private final VarInput<Boolean> fireWhenFocused = new VarInput<>(this, "Fire When Focused", Boolean.class, true);
    private final VarInput<Boolean> fireWhenAfk = new VarInput<>(this, "Fire When AFK", Boolean.class, false);
    private final VarInput<Number> fireWhenAfkSeconds = new VarInput<>(this, "Fire When AFK Seconds", Number.class, 0);
    private final VarInput<ExecSignal> exec = new VarInput<>(this, "Exec", ExecSignal.class, new ExecSignal(new String[0]));
    private final VarOutput<ExecSignal> execOut = new VarOutput<>(this, "Exec", ExecSignal.class, new ExecSignal(new String[0]));

    @Nullable
    private final VarInput<String> message;

    // Could maybe output "if fired" or something

    public ActionNode(Notification notification) {
        this.notification = notification;
        this.exec.setAllowMultipleConnections(true);

        if (notification instanceof MessageNotification) {
            this.message = new NotificationMessageInput(this, (MessageNotification) notification);
            this.reg(this.message);
        } else {
            this.message = null;
        }

        this.fireWhenFocused.setValue(this.notification.isFireWhenFocused());
        this.fireWhenFocused.onChange(this.notification::setFireWhenFocused);
        this.fireWhenAfk.setValue(this.notification.isFireWhenAFK());
        this.fireWhenAfk.onChange(this.notification::setFireWhenAFK);
        this.fireWhenAfkSeconds.setValue(this.notification.getFireWhenAFKForSeconds());
        this.fireWhenAfkSeconds.onChange((val) -> this.notification.setFireWhenAFKForSeconds(val.intValue()));

        reg(this.enabled);
        reg(this.fireWhenFocused);
        reg(this.fireWhenAfk);
        reg(this.fireWhenAfkSeconds);
        reg(this.exec);
        reg(this.execOut);
    }

    @Override
    public void process() {
        this.notification.setFireWhenFocused(this.fireWhenFocused.getValue());
        this.notification.setFireWhenAFK(this.fireWhenAfk.getValue());
        this.notification.setFireWhenAFKForSeconds(this.fireWhenAfkSeconds.getValue().intValue());
    }

    public void fire() {
        this.fire(this.exec.getValue().getCaptureGroups());
    }

    public void fire(String[] captureGroups) {
        this.dispatch(captureGroups, false);
        this.execOut.setValue(new ExecSignal(captureGroups));
    }

    /** Does not propagate exec downstream, unlike {@link #fire(String[])}. */
    public void fireForced(String[] captureGroups) {
        this.dispatch(captureGroups, true);
    }

    private void dispatch(String[] captureGroups, boolean forced) {
        String overrideMessage = this.getOverrideMessage();
        if (overrideMessage != null) {
            MessageNotification messageNotification = (MessageNotification) this.notification;
            if (forced) {
                messageNotification.fireForcedWithMessage(captureGroups, overrideMessage);
            } else {
                messageNotification.fireWithMessage(captureGroups, overrideMessage);
            }
        } else if (forced) {
            this.notification.fireForced(captureGroups);
        } else {
            this.notification.fire(captureGroups);
        }
    }

    /** Null means no override, so the notification should render its own message. */
    @Nullable
    private String getOverrideMessage() {
        return this.message != null && this.message.isConnected() ? this.message.getValue() : null;
    }

    /**
     * The action's "Message" input. Reports the connected value if something is connected, otherwise
     * the notification's own configured message.
     */
    private static final class NotificationMessageInput extends VarInput<String> {
        private final MessageNotification notification;

        private NotificationMessageInput(Node node, MessageNotification notification) {
            super(node, "Message", String.class, notification.getMessage());
            this.notification = notification;
        }

        @Override
        public String getValue() {
            return this.isConnected() ? super.getValue() : this.notification.getMessage();
        }

        @Override
        public VarOutput<String> toOutput() {
            return new VarOutput<>(this.getNode(), this.getName(), this.getType(), this.getValue());
        }
    }
}
