package io.github.composefluent.plugin.build

import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

@OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)
fun KotlinMultiplatformExtension.applyTargets(namespaceModule: String = "") {
    jvm()

    try {
        androidLibrary {
            compileSdk = 35
            namespace = "${BuildConfig.packageName}$namespaceModule"
        }
    } catch (_: IllegalStateException) {
        // handle exception when android library plugin was not applied
        androidTarget()
    }

    jvmToolchain(BuildConfig.Jvm.jvmToolchainVersion)
    wasmJs { browser() }
    js { browser() }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
    macosX64()

    applyDefaultHierarchyTemplate {
        sourceSetTrees(KotlinSourceSetTree.main, KotlinSourceSetTree.test)

        common {
            group("jvm") {
                withJvm()
            }

            group("skiko") {
                withJvm()
                group("native")
                group("web")
            }

            group("jvmCommon") {
                group("jvm")
                group("android")
            }

            group("desktop") {
                group("macos")
                group("mingw")
                group("linux")
                group("jvm")
            }

            group("android") {
                withAndroidTarget()
                withCompilations { it.target.name == "android" }
            }
        }

    }
}