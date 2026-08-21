plugins {
    id("frc-java-conventions")
}

dependencies {
    implementation(libs.timingframework.swing)
}

application {
    mainClass.set("PulseFieldDemo")
}
