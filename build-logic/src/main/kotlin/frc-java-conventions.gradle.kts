import gradle.kotlin.dsl.accessors._c2ac4b82f2f662e9adada5da2971f76a.java

plugins {
    `java-library`
    application
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

val javaToolchainLauncher = javaToolchains.launcherFor(java.toolchain)
val javaToolchainCompiler = javaToolchains.compilerFor(java.toolchain)

// Due to https://github.com/gradle/gradle/issues/18426, do not access `javaToolchains` from within the task scope
tasks {
    withType<JavaExec>().configureEach {
        //group = "class-with-main"
        classpath(sourceSets.main.get().runtimeClasspath)

        // Need to set the toolchain https://github.com/gradle/gradle/issues/16791
        javaLauncher.set(javaToolchainLauncher)

    }

    withType<JavaCompile>().configureEach {
        options.compilerArgs = listOf("-Xlint:deprecation")
        javaCompiler.set(javaToolchainCompiler)
        options.encoding = "UTF-8"
    }
}

// workaround for https://youtrack.jetbrains.com/issue/IDEA-316081/Gradle-8-toolchain-error-Toolchain-from-executable-property-does-not-match-toolchain-from-javaLauncher-property-when-different
gradle.taskGraph.whenReady {
    val ideRunTask = allTasks.find { it.name.endsWith(".main()") } as? JavaExec
    // note that javaLauncher property is actually correct
    @Suppress("UsePropertyAccessSyntax") // otherwise fails with: 'Val cannot be reassigned'
    ideRunTask?.setExecutable(javaToolchainLauncher.get().executablePath.asFile.absolutePath)
}
