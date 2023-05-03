# proguard-rules.pro
#-dontoptimize
#-dontobfuscate

-dontwarn kotlinx.**

-keepclasseswithmembers public class MainKt {
    public static void main(java.lang.String[]);
}
#-keep class org.jetbrains.skia.** { *; }
#-keep class org.jetbrains.skiko.** { *; }

-keep class kotlin.** { *; }
#-keep class org.jetbrains.skia.** { *; }
#-keep class org.jetbrains.skiko.** { *; }


#-assumenosideeffects public class androidx.compose.runtime.ComposerKt {
#    void sourceInformation(androidx.compose.runtime.Composer,java.lang.String);
#    void sourceInformationMarkerStart(androidx.compose.runtime.Composer,int,java.lang.String);
#    void sourceInformationMarkerEnd(androidx.compose.runtime.Composer);
#    boolean isTraceInProgress();
#    void traceEventStart(int, java.lang.String);
#    void traceEventEnd();
#}

# Kotlinx Coroutines Rules
# https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro

-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.SignalHandler
-dontwarn java.lang.instrument.Instrumentation
-dontwarn sun.misc.Signal
-dontwarn java.lang.ClassValue
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# https://github.com/Kotlin/kotlinx.coroutines/issues/2046
-dontwarn android.annotation.SuppressLint

# https://github.com/JetBrains/compose-jb/issues/2393
-dontnote kotlin.coroutines.jvm.internal.**
-dontnote kotlin.internal.**
-dontnote kotlin.jvm.internal.**
-dontnote kotlin.reflect.**
-dontnote kotlinx.coroutines.debug.internal.**
-dontnote kotlinx.coroutines.internal.**
## this is a weird one, but breaks build on some combinations of OS and JDK (reproduced on Windows 10 + Corretto 16)
#-dontwarn org.graalvm.compiler.core.aarch64.AArch64NodeMatchRules_MatchStatementSet*
