plugins {
    kotlin("jvm") version "2.0.0"
    id("io.papermc.paperweight.userdev") version "1.7.2"
    kotlin("plugin.serialization") version "2.0.0"
}

val pluginVersion: String by project

group = "com.liamxsage.shaderapi"
version = pluginVersion

val minecraftVersion: String by project
val slf4jVersion: String by project

val fruxzAscendVersion: String by project
val fruxzStackedVersion: String by project

val gsonVersion: String by project

val ktorVersion: String by project

val mcCoroutineVersion: String by project

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://nexus.flawcra.cc/repository/maven-mirrors/")
}

val deliverDependencies = listOf(
    "com.google.code.findbugs:jsr305:3.0.2",
    "com.google.code.gson:gson:$gsonVersion",

    "dev.fruxz:ascend:$fruxzAscendVersion",
    "dev.fruxz:stacked:$fruxzStackedVersion",
    "org.slf4j:slf4j-api:$slf4jVersion",

    "com.liamxsage:KlassicX:2024.7.13-18.48"
)

val includedDependencies = mutableListOf<String>()

fun Dependency?.deliver() = this?.apply {
    val computedVersion = version ?: kotlin.coreLibrariesVersion
    includedDependencies.add("${group}:${name}:${computedVersion}")
}

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    implementation(kotlin("stdlib")).deliver()
    implementation(kotlin("reflect")).deliver()

    deliverDependencies.forEach { dependency ->
        implementation(dependency).deliver()
    }
}

tasks.register("generateDependenciesFile") {
    group = "build"
    description = "Writes dependencies to file"

    val dependenciesFile = File(layout.buildDirectory.asFile.get(), "generated-resources/.dependencies")
    outputs.file(dependenciesFile)
    doLast {
        dependenciesFile.parentFile.mkdirs()
        dependenciesFile.writeText(includedDependencies.joinToString("\n"))
    }
}


tasks {
    build {
        dependsOn(reobfJar)
    }

    withType<ProcessResources> {
        dependsOn("generateDependenciesFile")

        from(File(layout.buildDirectory.asFile.get(), "generated-resources")) {
            include(".dependencies")
        }

        expand(
            "version" to project.version,
            "name" to project.name,
        )
    }

    register<JavaCompile>("compileMain") {
        source = fileTree("src/main/java")
        classpath = files(configurations.runtimeClasspath)
        destinationDirectory.set(file("build/classes/kotlin/main"))
        options.release.set(21)
    }
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/kotlin")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=kotlin.RequiresOptIn"
            )
        )
    }
}