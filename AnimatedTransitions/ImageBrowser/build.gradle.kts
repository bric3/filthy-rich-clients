plugins {
    id("frc-java-conventions")
}

description = "Builds, tests, and runs the project ImageBrowser."

dependencies {
    implementation(files("lib/AnimatedTransitions.jar"))
    implementation(files("lib/TimingFramework-0.55.jar"))
    // implementation(libs.timingframework.swing)
    // implementation(libs.jdesktop.animation.timing)
    // implementation(project(":java-net:animated-transitions-swing"))
}

application {
    mainClass.set("ImageBrowser")
}
