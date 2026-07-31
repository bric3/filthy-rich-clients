plugins {
    id("frc-java-conventions")
}

dependencies {
    implementation(libs.vlcj)
}

val vlcjJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")

application {
    mainClass.set("RepaintManagerDemo")
    applicationDefaultJvmArgs = vlcjJvmArgs
}

val testCard = layout.projectDirectory.file("src/main/resources/test-card-h265.mp4")

tasks.register<JavaExec>("runTestCard") {
    group = "application"
    description = "Runs the Repaint Manager demo with the bundled looping HEVC test card."
    mainClass.set(application.mainClass)
    jvmArgs(vlcjJvmArgs)
    args(testCard.asFile.absolutePath, "--loop")
}
