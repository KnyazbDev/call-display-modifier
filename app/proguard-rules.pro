# Add project specific ProGuard rules here.
-keep class com.calldisplaymodifier.app.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

