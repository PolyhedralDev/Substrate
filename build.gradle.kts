plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("me.champeau.jmh") version "0.6.6"
}

group = "com.dfsek"
version = "0.5.1"

repositories {
    mavenCentral()
    maven {
        name = "CodeMC"
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    implementation("commons-io:commons-io:2.11.0")
    implementation("net.jafama:jafama:2.3.2")
    implementation("org.ow2.asm:asm:9.2")

    implementation("io.vavr:vavr:0.10.4")

    jmh("org.openjdk.nashorn:nashorn-core:15.3")
    jmh("com.dfsek.terra:structure-terrascript-loader:0.1.0-BETA+21136f4c3")
    jmh("com.google.guava:guava:30.0-jre")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    useJUnitPlatform()
}