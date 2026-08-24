package com.adamk33n3r.nodegraph.nodes;

import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.runelite.watchdog.notifications.MessageNotification;

import lombok.Getter;

/**
 * An {@link ActionNode} for notifications that render a message. The "Message" input is forwarded to
 * {@link MessageNotification#setMessage(String)}, so the notification fires with whatever the graph
 * last supplied — the same way {@code fireWhenFocused} and friends are forwarded.
 */
@Getter
public class MessageActionNode extends ActionNode {
    private final VarInput<String> message = new VarInput<>(this, "Message", String.class, "");

    public MessageActionNode(MessageNotification notification) {
        super(notification);

        this.message.setValue(notification.getMessage());
        this.message.onChange(notification::setMessage);

        this.reg(this.message);
    }

    public MessageNotification getMessageNotification() {
        return (MessageNotification) this.getNotification();
    }
}
