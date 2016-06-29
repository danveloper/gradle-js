package com.github.danveloper.gradle.plugins.jsbuild.internal

import jdk.nashorn.api.scripting.AbstractJSObject
import jdk.nashorn.api.scripting.JSObject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.dsl.ScriptHandler

class BetterProject {

  @Delegate
  Project project

  BetterProject(Project project) {
    this.project = project
  }

  void artifacts(Action<ArtifactHandler> fn) {
    fn.execute(project.getArtifacts())
  }

  void ant(Action<AntBuilder> fn) {
    fn.execute(project.getAnt())
  }

  void configurations(Action<ConfigurationContainer> fn) {
    fn.execute(project.getConfigurations())
  }

  void buildscript(Action<BetterScriptHandler> fn) {
    fn.execute(new BetterScriptHandler(delegate: project.buildscript))
  }

  void repositories(Action<RepositoryHandler> fn) {
    fn.execute(project.getRepositories())
  }

  void dependencies(Action<JSObject> fn) {
    fn.execute(new AbstractJSObject() {
      @Override
      Object getMember(String name) {
        new DelegatingObject(method: name, handler: project.dependencies)
      }
    })
  }

  void task(String taskName, SweeterAction<Task> fn) {
    def task = project.tasks.create(taskName)
    fn.execute(new BetterTask(task: task));
  }

  static class BetterScriptHandler {
    @Delegate
    ScriptHandler delegate

    void repositories(Action<RepositoryHandler> action) {
      action.execute(getRepositories())
    }

    void dependencies(Action<JSObject> action) {
      action.execute(new AbstractJSObject() {
        @Override
        Object getMember(String name) {
          new DelegatingObject(method: name, handler: delegate.dependencies)
        }
      })
    }
  }

  static class BetterTask {
    @Delegate
    Task task

    void action(Action<Task> action) {
      actions.add(action)
    }
  }

  static class DelegatingObject extends AbstractJSObject {
    def handler
    String method

    Object call(Object thiz, Object... args) {
      handler."${method}"(args as Object[])
    }
  }
}
