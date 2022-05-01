plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("me.champeau.jmh") version "0.6.6"
}

group = "com.dfsek"
version = "0.5.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    implementation("commons-io:commons-io:2.11.0")
    implementation("net.jafama:jafama:2.3.2")
    implementation("org.ow2.asm:asm:9.2")

    implementation("io.vavr:vavr:0.10.4")

    jmh("org.openjdk.nashorn:nashorn-core:15.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    useJUnitPlatform()
}