import java.io.File
import java.net.URI
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "com.kotonosora.numrise"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kotonosora.numrise"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.accompanist.permissions)
    implementation(libs.play.services.location)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.core)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    implementation(libs.moshi.kotlin)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.material)
    implementation(libs.billing.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.runner)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    "ksp"(libs.androidx.room.compiler)
    "ksp"(libs.moshi.kotlin.codegen)
}

tasks.register("generateAssets") {
    group = "assets"
    description = "Downloads font and generates sound effects"

    val fontFile = file("src/main/res/font/press_start_2p.ttf")
    val rawDir = file("src/main/res/raw")

    doLast {
        // Create directories if they don't exist
        fontFile.parentFile.mkdirs()
        rawDir.mkdirs()

        // 1. Download Font
        println("Downloading font...")
        try {
            URI("https://raw.githubusercontent.com/google/fonts/main/ofl/pressstart2p/PressStart2P-Regular.ttf").toURL().openStream().use { input ->
                fontFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            println("Failed to download font: \${e.message}")
        }

        // 2. Generate Sounds
        println("Generating sounds...")
        generateWavFile(File(rawDir, "tap.wav"), 880.0, 0.1)     // A5, 100ms
        generateWavFile(File(rawDir, "error.wav"), 220.0, 0.3)   // A3, 300ms
        generateWavFile(File(rawDir, "win.wav"), listOf(440.0, 554.37, 659.25, 880.0), 0.5) // A4, C#5, E5, A5
        generateWavFile(File(rawDir, "lose.wav"), listOf(440.0, 349.23, 293.66, 220.0), 0.5) // A4, F4, D4, A3
    }
}

fun generateWavFile(file: File, frequencies: Any, duration: Double) {
    val sampleRate = 44100
    val numSamples = (duration * sampleRate).toInt()
    val data = ShortArray(numSamples)
    
    val freqList = if (frequencies is List<*>) {
        @Suppress("UNCHECKED_CAST")
        frequencies as List<Double>
    } else {
        listOf(frequencies as Double)
    }
    val samplesPerFreq = numSamples / freqList.size

    for (i in 0 until numSamples) {
        val freqIndex = (i / samplesPerFreq).coerceAtMost(freqList.size - 1)
        val freq = freqList[freqIndex]
        val time = i.toDouble() / sampleRate
        // Basic square wave for retro feel
        val value = if (sin(2.0 * PI * freq * time) > 0) Short.MAX_VALUE / 2 else -Short.MAX_VALUE / 2
        data[i] = value.toShort()
    }

    file.outputStream().use { out ->
        // RIFF header
        out.write("RIFF".toByteArray())
        out.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(36 + numSamples * 2).array())
        out.write("WAVE".toByteArray())
        // fmt chunk
        out.write("fmt ".toByteArray())
        out.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(16).array())
        out.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(1).array()) // PCM
        out.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(1).array()) // Mono
        out.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(sampleRate).array())
        out.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(sampleRate * 2).array())
        out.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(2).array())
        out.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(16).array())
        // data chunk
        out.write("data".toByteArray())
        out.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(numSamples * 2).array())
        val buffer = ByteBuffer.allocate(numSamples * 2).order(ByteOrder.LITTLE_ENDIAN)
        for (sample in data) buffer.putShort(sample)
        out.write(buffer.array())
    }
}
