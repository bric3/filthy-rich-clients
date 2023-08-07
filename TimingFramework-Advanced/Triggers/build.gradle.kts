plugins {
    id("frc-java-conventions")
}

dependencies {
    implementation(libs.jdesktop.animation.timing)
}

application {
    mainClass.set("Triggers")
}
