package com.github.danveloper.gradle.plugins.jsbuild.internal

@FunctionalInterface
interface SweeterAction<T> {

  void execute(T t) throws Exception
}
