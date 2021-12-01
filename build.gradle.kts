plugins {
    java
    id("com.github.johnrengelman.shadow").version("6.1.0")
}

group = "com.dfsek"
version = "0.5.1"

repositories {
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("commons-io:commons-io:2.8.0")
    implementation("net.jafama:jafama:2.3.2")
    implementation("org.ow2.asm:asm:9.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Test>() {
    useJUnitPlatform()
}