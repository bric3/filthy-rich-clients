plugins {
    id("frc-java-conventions")
}

// More JOGL example there:
// https://github.com/jvm-graphics-labs/modern-jogl-examples

dependencies {
    implementation(libs.bundles.jogamp)
}

// JOGL 2.5.0 and later can find the first thread on their own,
// using -XstartOnFirstThread is not anymore necessary, worse it blocks Swing thread
// https://discourse.vtk.org/t/vtk-java-mac-swing/2794/7

application {
    mainClass.set("BloomOpenGL")
}