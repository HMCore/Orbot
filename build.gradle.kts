import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.4.10"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("org.jetbrains.kotlin.kapt") version kotlinVersion
}

group = "de.wulkanat"
version = "2.0.0"

repositories {
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/koltinx")
    maven("https://dl.bintray.com/kordlib/Kord")
}

dependencies {
    testImplementation(kotlin("test-junit"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jsoup:jsoup:1.13.1")

    implementation("dev.kord:kord-common:0.7.0-RC")
    implementation("com.gitlab.kordlib.kordx:kordx-commands-runtime-kord:0.3.4")
    implementation("com.gitlab.kordlib:kordx.emoji:0.4.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    kapt("com.gitlab.kordlib.kordx:kordx-commands-processor:0.3.4")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(Pair("Main-Class", "de.wulkanat.MainKt")))
    }
}

tasks.create<Jar>("fatJar") {
    archiveBaseName.set("${project.name}-all")
    manifest {
        attributes["Implementation-Version"] = archiveVersion
        attributes["Main-Class"] = "de.wulkanat.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}