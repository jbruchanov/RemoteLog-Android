package com.scurab.android;

import android.app.Activity;
import android.view.KeyEvent;

/**
 * Simple Volume SOS listener
 * This class is helper for taking screenshot of app for users.
 * <br/>
 * In your activity override {@link Activity#onKeyDown(int, KeyEvent)} and send every event to
 * {@link #dispatchKeyEvent(KeyEvent)} before passing to parent class.
 * <br/>
 * SOS - VolUp VolUp VolUp VolDown VolDown VolDown VolUp VolUp VolUp :)<br/>
 * Screenshot will be taken and send to server
 *
 * @author Jiri Bruchanov
 */
public class SOSListener {

    public static final int VOLUME_CLICK_LIMIT = 20;

    public interface OnSOSListener {
        public void onSOS();
    }

    public enum Strategy{
        SOS,
        LONG_VOLUME_DOWN
    }

    private static final int STATE_START = 0;
    private static final int STATE_S = 1;
    private static final int STATE_SO = 2;

    /**
     * Current state *
     */
    private int mState = 0;

    /**
     * KeyPress counter *
     */
    private int mCounter = 0;

    private Strategy mStrategy = Strategy.LONG_VOLUME_DOWN;

    private final OnSOSListener mListener;

    public SOSListener(OnSOSListener listener) {
        this(Strategy.LONG_VOLUME_DOWN, listener);
    }

    public SOSListener(Strategy strategy, OnSOSListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener is null!");
        }
        mStrategy = strategy;
        mListener = listener;
    }

    /**
     * Call this method from activity to proper functionality
     * @param key
     */
    public void dispatchKeyEvent(KeyEvent key){
        if(mStrategy == Strategy.SOS){
            dispatchKeyEventSOS(key);
        }else if(mStrategy == Strategy.LONG_VOLUME_DOWN){
            dispatchKeyEventVolumeDown(key);
        }
    }

    /**
     * Handle keyEvent for simple volume down long click...
     * @param key
     */
    private void dispatchKeyEventVolumeDown(KeyEvent key) {
        int keyCode = key.getKeyCode();
        // handle only if volume keys are clicked
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            mCounter++;
        } else{
            reset();
        }

        if(mCounter > VOLUME_CLICK_LIMIT){
            mListener.onSOS();
            reset();
        }
    }

    /**
     * Handle keyEvent for moving in state machine
     *
     * @param key
     */
    private void dispatchKeyEventSOS(KeyEvent key) {
        int keyCode = key.getKeyCode();
        // handle only if volume keys are clicked
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            switch (mState) {
                case STATE_START:
                case STATE_SO: {
                    // if VolUP increase counter, and keep it on 3 if is more than 3
                    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                        mCounter++;
                        if (mState == STATE_START) {
                            if (mCounter > 3) {
                                mCounter = 3;
                            }
                        } else if (mState == STATE_SO) {
                            if (mCounter == 3) {
                                //we pressed UUU DDD UUU => notify
                                mListener.onSOS();
                                //and reset machine to start state
                                reset();
                            }
                        }
                    } else {
                        // if 3 and Down, move to next state
                        if (mCounter == 3) {
                            mState = STATE_S;
                            mCounter = 1;
                        }
                    }
                }
                break;
                case STATE_S: {
                    // if VolUP increase counter, and keep it on 3 if is more than 3
                    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                        mCounter++;
                        if (mCounter > 3) {
                            reset();
                        }
                    } else {
                        // if 3 and Down, move to next state
                        if (mCounter == 3) {
                            mState = STATE_SO;
                            mCounter = 1;
                        }
                    }
                }
                break;
            }
        } else {
            reset();
        }
    }

    /**
     * Reset state machine to start position
     */
    public void reset() {
        mCounter = 0;
        mState = STATE_START;
    }
}
