package com.github.danveloper.gradle.plugins.jsbuild

import org.gradle.api.Project

class JsBuildExtension {

  File scripts

  JsBuildExtension(Project project) {
    def scriptsDir = project.file("scripts")
    if (scriptsDir.exists()) {
      scripts = scriptsDir
    }
  }
}
