package com.n1xend.authlite.module;

public interface Module {
    boolean isEnabled();
    void enable();
    default void disable() {}
}
