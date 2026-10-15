# CodeAlpha_Android_QuoteApp

A Native **Android Mobile Application** developed in **Java** for the **CodeAlpha Mobile & Web Application Development Internship (Task 2)**, integrated with the **Java REST Backend**.

![Android](https://img.shields.io/badge/Platform-Android%20%7C%20Java-3DDC84?style=for-the-badge&logo=android)
![Backend](https://img.shields.io/badge/Backend-Java%20HTTP%20REST-orange?style=for-the-badge&logo=openjdk)

---

## 🌟 Mobile Features

- 🎲 **Random Quotes**: Displays inspirational quotes on launch and with every tap of the **"New Quote"** button.
- 🔊 **Native Text-to-Speech (TTS)**: Uses Android's `TextToSpeech` engine to read quotes and authors aloud.
- 📋 **Clipboard Copy**: Copies quotes directly to the Android system clipboard.
- 📲 **Social Share Intent**: Android native share sheet allowing instant sharing to WhatsApp, Twitter, LinkedIn, etc.
- 🌐 **Java Backend Synchronization**: Pulls random quotes from `CodeAlpha_Java_Backend` running on port 8080 (`http://10.0.2.2:8080/api/quotes?random=true`).

---

## 🚀 How to Run in Android Studio

1. Open **Android Studio**.
2. Select **File > Open...** and choose `CodeAlpha_Android_QuoteApp`.
3. Allow Gradle to sync.
4. Run on an Android Emulator or connected physical phone.
