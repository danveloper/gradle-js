package com.github.danveloper.gradle.plugins.jsbuild.internal;

@FunctionalInterface
public interface BetterFactory<T> {

  T create() throws Exception;

}
