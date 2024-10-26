plugins {
    id("java")
    id("application")
}

description = "OpenTelemetry Log Appender Example"

repositories {
    mavenCentral()
}

dependencies {
    // Log4j
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.24.1"))
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")

    // OpenTelemetry
    implementation(platform("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom-alpha:2.9.0-alpha"))
    implementation("io.opentelemetry:opentelemetry-sdk")
    implementation("io.opentelemetry:opentelemetry-exporter-logging")
    implementation("io.opentelemetry.instrumentation:opentelemetry-log4j-appender-2.17")
}

application {
    mainClass = "io.opentelemetry.example.logappender.Application2"
}
