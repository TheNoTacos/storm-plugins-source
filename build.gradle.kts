import ProjectVersions.stormVersion

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    `java-library`
    checkstyle
    kotlin("jvm") version "1.6.21"
}

project.extra["GithubUrl"] = "https://github.com/WaRpProjects/StormPlugins"
project.extra["GithubUserName"] = "WaRpProjects"
project.extra["GithubRepoName"] = "StormPlugins"

apply<BootstrapPlugin>()

allprojects {
    group = "net.storm.plugins"

    project.extra["PluginProvider"] = "WaRp"
    project.extra["ProjectSupportUrl"] = "https://discord.gg/WTvTbSPknJ"
    project.extra["PluginLicense"] = "3-Clause BSD License"

    apply<JavaPlugin>()
    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "checkstyle")

    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://repo.storm-client.net/dev/")
            credentials {
                username = secrets.REPO_KEY
                password = secrets.REPO_BUILD
            }
        }
    }

    dependencies {
        annotationProcessor(Libraries.lombok)
        annotationProcessor(Libraries.pf4j)

        compileOnly("net.unethicalite:http-api:$stormVersion+")
        compileOnly("net.unethicalite:runelite-api:$stormVersion+")
        compileOnly("net.unethicalite:runelite-client:$stormVersion+")

        compileOnly(Libraries.guice)
        compileOnly(Libraries.javax)
        compileOnly(Libraries.lombok)
        compileOnly(Libraries.pf4j)
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
            dirMode = 493
            fileMode = 420
        }

        compileKotlin {
            kotlinOptions.jvmTarget = "11"
        }
    }
}
