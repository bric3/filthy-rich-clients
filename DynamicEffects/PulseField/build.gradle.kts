plugins {
    id("frc-java-conventions")
}

dependencies {
    implementation(files("lib/TimingFramework-0.54.jar"))
    // implementation(libs.jdesktop.animation.timing)
}

application {
    mainClass.set("PulseFieldDemo")
}
