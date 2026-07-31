plugins {
    id("frc-java-conventions")
}

dependencies {
    implementation(files("lib/AnimatedTransitions.jar"))
    implementation(files("lib/TimingFramework-0.55.jar"))
    // implementation(libs.timingframework.swing)
    // implementation(libs.jdesktop.animation.timing)
    // implementation(project(":java-net:animated-transitions-swing"))
}

application {
    mainClass.set("SearchTransition")
}

tasks {
    withType<JavaExec>().configureEach {
        // for lib/AnimatedTransitions.jar
        // making JComponent::setFlag accessible
        jvmArgs("--add-opens=java.desktop/javax.swing=ALL-UNNAMED")
    }
}
