plugins {
    id("frc-java-conventions")
}

description = "Builds, tests, and runs the project ImageBrowser."

dependencies {
    // implementation(libs.timingframework.swing)
    implementation(libs.jdesktop.animation.timing)
    implementation(project(":java-net:animated-transitions-swing"))
}

