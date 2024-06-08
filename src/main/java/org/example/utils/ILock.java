package org.example.utils;

public interface  ILock {

    boolean trylock(long timeoutSec);
    void unlock();


}
