plugins {
    `kotlin-dsl`
}

group = "com.hadi.abbasi.notnow.buildlogic"

dependencies {
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("kotlinLibrary") {
            id = libs.plugins.notnow.kotlin.library.get().pluginId
            implementationClass = "KotlinLibraryConventionPlugin"
        }
    }
}