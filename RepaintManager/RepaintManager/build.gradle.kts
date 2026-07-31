plugins {
    id("frc-java-conventions")
}

dependencies {
    implementation(libs.vlcj)
}

application {
    mainClass.set("RepaintManagerDemo")
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}
