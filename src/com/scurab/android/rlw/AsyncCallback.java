package com.scurab.android.rlw;

/**
 * Simple generic callback interface
 *
 * @param <T>
 * @author Jiri Bruchanov
 */
public interface AsyncCallback<T> {
    void call(T value);
}
