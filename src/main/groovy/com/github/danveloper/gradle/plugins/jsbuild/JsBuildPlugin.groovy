package com.github.danveloper.gradle.plugins.jsbuild

import com.github.danveloper.gradle.plugins.jsbuild.internal.BetterFactory
import com.github.danveloper.gradle.plugins.jsbuild.internal.BetterProject
import com.github.danveloper.gradle.plugins.jsbuild.internal.BigStupidFunction
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * Apply the shit out of this plugin.
 *
 * <p>
 *   buildscript {
 *     dependencies {
 *       classpath 'com.github.danveloper:gradle-js:1.0.0'
 *     }
 *   }
 *   apply plugin: 'js-build'
 * </p>
 *
 * @author Dan Woods
 * @since The Beginning.
 */
@CompileStatic
public class JsBuildPlugin implements Plugin<Project> {
  protected static ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
  protected static Bindings bindings = engine.createBindings();

  @Override
  public void apply(Project project) {
    JsBuildExtension extension = (JsBuildExtension)project.extensions.create("jsbuild", JsBuildExtension, project)

    bindings.put("project", new BetterProject(project));
    engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
    if (extension.scripts && extension.scripts.directory) {
      project.fileTree(extension.scripts.absolutePath).each { file ->
        if (file.name.endsWith(".js")) {
          loadScript(file)
        }
      }
    }

    File buildScript = new File(project.getProjectDir(), "gradle.js");
    loadScript(buildScript)
  }

  private static void loadScript(File script) {
    uncheck {
      InputStream stream = new FileInputStream(script);
      byte[] buf = uncheck0 { new byte[stream.available()] }
      stream.read(buf);
      engine.eval(new String(buf));
    }
  }

  private static void uncheck(BigStupidFunction fn) {
    uncheck0 {
      fn.functionate();
      return null;
    }
  }

  private static <T> T uncheck0(BetterFactory<T> factory) {
    try {
      return factory.create();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
