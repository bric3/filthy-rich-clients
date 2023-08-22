plugins {
    id("frc-java-conventions")
}

// More JOGL example there:
// https://github.com/jvm-graphics-labs/modern-jogl-examples

repositories {
    maven {
        url = uri("https://jogamp.org/deployment/maven")
    }
}

dependencies {
    implementation(libs.bundles.jogamp)
}

// Seems like jogl 2.5.0 is able to find the first thread on its own,
// using -XstartOnFirstThread is not anymore necessary, worse it blocks Swing thread
// https://discourse.vtk.org/t/vtk-java-mac-swing/2794/7