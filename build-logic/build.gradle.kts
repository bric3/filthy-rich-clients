plugins {
    // doc: https://docs.gradle.org/current/userguide/kotlin_dsl.html
    `kotlin-dsl`
}

dependencies {
    // https://github.com/gradle/gradle/issues/15383
    // implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}