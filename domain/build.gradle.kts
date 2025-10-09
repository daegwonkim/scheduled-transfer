plugins {
    id("java")
}

group = "io.github.daegwon"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}