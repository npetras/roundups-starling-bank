plugins {
    id("java")
    id("application")
}

group = "com.nicolaspetras"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.apache.commons:commons-text:1.10.0")

    // logging
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
}

application {
    mainClass.set("com.nicolaspetras.roundups.Main")
}
