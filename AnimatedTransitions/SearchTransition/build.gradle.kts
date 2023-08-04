plugins {
    id("frc-java-conventions")
}

dependencies {
    // implementation(libs.timingframework.swing)
    implementation(libs.jdesktop.animation.timing)
    implementation(project(":java-net:animated-transitions-swing"))
}
