package com.github.danveloper.gradle.plugins.jsbuild

import org.gradle.api.plugins.GroovyPlugin
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

class JsBuildPluginSpec extends PluginSpecification {

  void 'apply plugin from build js'() {
    given:
    def project = buildJs("""\
      project.apply({plugin: 'groovy'})
    """.stripIndent())

    when:
    project.plugins.apply(JsBuildPlugin)

    then:
    project.plugins.hasPlugin(GroovyPlugin)
  }

  void 'set repositories from build js'() {
    given:
    def project = buildJs("""
      project.repositories(function(repos) {
        repos.jcenter();
      });
    """)

    when:
    project.plugins.apply(JsBuildPlugin)

    then:
    project.repositories.getByName("BintrayJCenter")
  }

  void 'set dependencies from build js'() {
    given:
    def project = buildJs("""
      project.apply({plugin: 'java'});

      project.dependencies(function(deps) {
        deps.compile('io.ratpack:ratpack-core:1.3.3')
      });
    """)

    when:
    project.plugins.apply(JsBuildPlugin)

    then:
    project.configurations["compile"].dependencies.size() == 1
    project.configurations["compile"].dependencies[0].group == 'io.ratpack'
    project.configurations["compile"].dependencies[0].name == 'ratpack-core'
    project.configurations["compile"].dependencies[0].version == '1.3.3'
  }

  void 'should be able to create a task'() {
    setup:
    def project = buildJs("""
      project.task('myTask', function(task) {
        task.action(function(task) {
          print('myTask worked');
        });
      });
    """)
    project.file("build.gradle") << """
      plugins {
        id 'js-build'
      }
    """

    when:
    def result = GradleRunner.create()
        .withProjectDir(project.projectDir)
        .withArguments("myTask")
        .withPluginClasspath()
        .build()

    then:
    result.output.contains("myTask worked")
    result.task(":myTask").outcome == TaskOutcome.SUCCESS
  }

  void 'should respect doLast on tasks'() {
    setup:
    def project = buildJs("""
      project.task('myTask', function(task) {
        task.doLast(function(task) {
          print('doLast worked')
        });
      });
    """)
    project.file("build.gradle") << """
      plugins {
        id 'js-build'
      }
    """

    when:
    def result = GradleRunner.create()
        .withProjectDir(project.projectDir)
        .withArguments("myTask")
        .withPluginClasspath()
        .build()

    then:
    result.output.contains("doLast worked")
    result.task(":myTask").outcome == TaskOutcome.SUCCESS
  }

  void 'should allow loading of additional js files from project'() {
    setup:
    def project = buildJs("""
      project.task('myTask', function(task) {
        task.action(function(task) {
          print(foo);
        });
      });
    """)
    project.file("scripts").mkdir()
    new File(project.file("scripts"), "foo.js") << "foo = 'bar';"
    project.file("build.gradle") << """
      plugins {
        id 'js-build'
      }
      jsbuild {
        println "scripts"
        scripts = file("scripts")
      }
    """

    when:
    def result = GradleRunner.create()
        .withProjectDir(project.projectDir)
        .withArguments("myTask")
        .withPluginClasspath()
        .withDebug(true)
        .build()

    then:
    result.output.contains("bar")
  }
}
