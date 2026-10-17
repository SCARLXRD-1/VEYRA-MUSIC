# Keep enough source information for useful release crash reports.
-keepattributes SourceFile,LineNumberTable

# ONNX Runtime's JNI layer resolves its Java classes, fields and constructors by
# hardcoded name from native code (FindClass/GetMethodID/GetFieldID). R8 renaming
# or stripping any of them turns into `java_class == null` and a SIGABRT inside
# convertToTensorInfo the first time a model runs, so the whole package must
# survive shrinking untouched.
-keep class ai.onnxruntime.** { *; }
-keep class ai.onnxruntime.providers.** { *; }
-dontwarn ai.onnxruntime.**

# LiteRT's JNI layer also resolves Java exception/API classes by hardcoded name.
# R8 renaming `LiteRtException` makes nativeCreateFromAsset abort the process at
# first playback analysis with "Failed to find LiteRtException class".
-keep class com.google.ai.edge.litert.** { *; }
-dontwarn com.google.ai.edge.litert.**

# WebRTC's native layer calls back into Java by hardcoded name, the same way ONNX
# Runtime's does: observers, the enums it reads signalling state from, and the
# constructors JNI instantiates are all resolved reflectively. Renaming any of them
# fails at the first setRemoteDescription rather than at build time.
-keep class org.webrtc.** { *; }
-dontwarn org.webrtc.**

# WebRTC reaches Java through Chromium's jni_zero layer, whose C++ half looks up
# org.jni_zero.JniInit by name from JNI_OnLoad, before any Java code has run. Nothing
# on the Java side names that class, so shrinking drops it, and the native loader then
# aborts the process outright: a bare SIGTRAP with no Java frames, raised by
# System.loadLibrary at the first PeerConnectionFactory.initialize rather than anywhere
# near the party code that asked for it.
-keep class org.jni_zero.** { *; }
-dontwarn org.jni_zero.**

# Los bindings generados del motor de transiciones llegan a Rust mediante JNA, que
# resuelve todo por nombre en tiempo de ejecución y por eso no sobrevive a un renombrado.
# `Native.register` vincula cada `external fun` al símbolo nativo *llamado igual*, y JNA lee
# los campos de sus subclases de Structure por reflexión, así que un método renombrado o un
# campo eliminado produce un UnsatisfiedLinkError en la primera transición en lugar de un
# fallo de compilación. JNA también referencia clases AWT de escritorio que Android no
# tiene, en rutas que Veyra Music nunca alcanza.
-keep class com.sun.jna.** { *; }
-keepclassmembers class * extends com.sun.jna.** { public *; }
-keep class dev.sfg.veyra.earmark.** { *; }
-dontwarn java.awt.**

# NewPipeExtractor embeds Mozilla Rhino for YouTube cipher evaluation.
-keep class org.mozilla.javascript.** { *; }
-keep class org.mozilla.classfile.ClassFileWriter
-dontwarn org.mozilla.javascript.tools.**

# The native Best Mix scorer exports a name-based JNI symbol for this Kotlin object.
-keep class dev.sfg.veyra.mobile.playback.smart.NativeBestMixPlanner { *; }

# Rhino también contiene integraciones opcionales de scripting/beans del JDK. Android
# no provee esas APIs exclusivas de escritorio, y Veyra Music usa el motor central de
# Rhino para la evaluación de cifrado en lugar de sus adaptadores javax.script o dynalink.
-dontwarn java.beans.**
-dontwarn javax.script.**
-dontwarn jdk.dynalink.**
