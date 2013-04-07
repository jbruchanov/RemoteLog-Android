package com.scurab.android;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import com.scurab.android.rlw.RLog;
import com.scurab.gwt.rlw.shared.model.PushMessage;

/**
 * Simple service which is used for sending log items by {@link Notification}
 *
 * @author Jiri Bruchanov
 */
public class RespondService extends IntentService {

    public RespondService() {
        super("RemoteWebLog.RespondService");
    }

    public RespondService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RLog.n(this, "PushRespond", getMessage(intent));
    }

    private String getMessage(Intent i) {
        return String.format("Action:%s, Message:[%s]", i.getAction(), i.getExtras().getSerializable(PushMessage.class.getSimpleName()));
    }
}
