package com.scurab.android.rlw;

/**
 * Simple generic callback interface
 * @author Joe Scurab
 *
 * @param <T>
 */
public interface AsyncCallback<T> {
    void call(T value);
}
