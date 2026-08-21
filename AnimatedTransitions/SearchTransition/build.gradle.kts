plugins {
    id("frc-java-conventions")
}

dependencies {
    implementation(libs.timingframework.swing)
    implementation(project(":java-net:animated-transitions-swing"))
}

application {
    mainClass.set("SearchTransition")
}
