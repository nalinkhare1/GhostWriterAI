# GhostWriterAI
🪄 Ghost Writer: Sketch-to-Code with Gemini AI 

Ghost Writer is an intelligent Android application that bridges the gap between low-fidelity paper wireframes and functional, production-ready code. Using Google's Gemini 1.5 Flash, it transforms a simple photo of a hand-drawn sketch into responsive UI code in seconds.

✨ Key Features
•
📸 Point & Shoot: Capture sketches directly via CameraX with real-time UI overlays to guide your composition.
•
🚀 Multi-Framework Export: Generate code for:
◦
Vanilla HTML + Tailwind CSS
◦
React + Tailwind
◦
Bootstrap 5
◦
Jetpack Compose (Material 3)
•
🪄 Magic Tools: AI-driven refinements at the tap of a button:
◦
Magic Assets: Replaces placeholders with real Unsplash images.
◦
Magic Copy: Replaces "Lorem Ipsum" with high-converting marketing text.
◦
Accessibility Audit: Automatically adds ARIA roles and fixes contrast.
◦
Dark Mode: Generates a premium dark theme palette.
•
📱 Responsive Preview: Toggle between Mobile, Tablet, and Desktop views instantly.
•
💾 Project History: Full offline persistence using Room Database.
•
📦 ZIP Export: Share your generated project as a ZIP file directly to your dev environment.
•
🕹️ Demo Mode: Explore the app's potential even without an API key using hardcoded demo scenarios.

🛠️ Tech Stack
•
Language: Kotlin
•
UI Framework: Jetpack Compose (100%)
•
AI Engine: Google Gemini AI SDK (generativeai)
•
Architecture: MVVM with StateFlow
•
Local Database: Room
•
Camera: CameraX
•
Networking: Kotlin Coroutines & Flow
•
Dependency Management: Version Catalog (libs.versions.toml)

🚀 Getting Started
Prerequisites
•
Android Studio Ladybug or newer.
•
A Gemini API Key (Optional for Demo Mode, required for AI features). Get one here.

Installation
1. Clone the repository:
Shell Script
git clone https://github.com/your-username/ghost-writer.git
2. Open the project in Android Studio.
3. Sync Gradle and run the app on an emulator or physical device.
4. (Optional) Enter your API Key in the Settings menu.


📖 How It Works
1.
Sketch: Draw your UI idea on a napkin, whiteboard, or paper.
2.
Capture: Use the in-app camera to take a clear photo.
3.
Refine: Tell Ghost Writer to "Make the buttons rounded" or "Add a search bar" via the chat-based refinement tool.
4.
Export: Share the ZIP or copy the code to your clipboard.

🛡️ Privacy & Security
Ghost Writer stores your Gemini API key securely in SharedPreferences. Images are processed by Google Gemini and are not stored permanently on any third-party server by this application.



📄 License
Distributed under the MIT License. See LICENSE for more information.

#Android, #Kotlin, #Gemini, #GenerativeAI, and #JetpackCompose



