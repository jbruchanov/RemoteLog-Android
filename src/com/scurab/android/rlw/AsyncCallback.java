package com.scurab.android.rlw;

/**
 * Simple generic callback interface
 * @author Jiri Bruchanov
 *
 * @param <T>
 */
public interface AsyncCallback<T> {
    void call(T value);
}
