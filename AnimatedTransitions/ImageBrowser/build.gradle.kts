plugins {
    id("frc-java-conventions")
}

description = "Builds, tests, and runs the project ImageBrowser."

dependencies {
    implementation(libs.timingframework.swing)
    implementation(project(":java-net:animated-transitions-swing"))
}

application {
    mainClass.set("ImageBrowser")
}

tasks {
    withType<JavaExec>().configureEach {
        workingDir = projectDir
    }
}
