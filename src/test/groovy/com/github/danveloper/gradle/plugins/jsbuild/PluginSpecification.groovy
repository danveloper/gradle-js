package com.github.danveloper.gradle.plugins.jsbuild

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class PluginSpecification extends Specification {

  @Rule
  TemporaryFolder dir

  Project buildJs(String content) {
    File projectDir = dir.newFolder()
    Project project = ProjectBuilder.builder().withProjectDir(projectDir).withName("proj").build()
    File buildJs = new File(projectDir, 'gradle.js')
    buildJs << content
    project
  }

  public static File getTestKitDir() {
    def gradleUserHome = System.getenv("GRADLE_USER_HOME")
    if (!gradleUserHome) {
      gradleUserHome = new File(System.getProperty("user.home"), ".gradle").absolutePath
    }
    return new File(gradleUserHome, "testkit")
  }
}
