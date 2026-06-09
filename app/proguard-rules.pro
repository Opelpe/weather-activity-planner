# Readable crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# kotlinx.serialization — compiler plugin generates $$serializer classes accessed by name
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.pnow.**$$serializer { *; }
-keepclassmembers class com.pnow.** {
    *** Companion;
}
-keepclasseswithmembers class com.pnow.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit — keep suspend function signatures used by coroutines continuation
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
