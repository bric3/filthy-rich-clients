plugins {
    id("frc-java-conventions")
}

dependencies {
    implementation(files("lib/TimingFramework.jar"))
    // implementation(libs.timingframework.swing)
    // implementation(libs.jdesktop.animation.timing)
}

application {
    mainClass.set("org.progx.artemis.Application")
}
