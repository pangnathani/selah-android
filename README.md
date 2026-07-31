# Selah: An AI-Powered Digital Sanctuary

## The Problem: Digital Anesthetics
We don't just scroll to consume content; we scroll to escape. Studies show that 73% of heavy social media users report high levels of loneliness and isolation. We use infinite feeds as a digital anesthetic to numb feelings of burnout, anxiety, or emptiness. The tragic irony is that in the exact moments we feel the most emotionally drained—when we most desperately need the truth and comfort of God’s Word—we are glued to our screens.

## What We Built
Selah is a native Android digital wellbeing application designed to break the cycle of mindless doomscrolling by actively intervening with AI-guided, Scripture-based reflection.

## How it Works & Core Features:

* **System-Level Intervention**: Built natively in Kotlin, Selah utilizes the Android UsageStats API to securely monitor digital habits in the background. When a user hits a continuous 15-minute scrolling limit on guarded apps, Selah drops a system-level UI shield that interrupts the feed.
* **The Digital Sanctuary**: Users are prompted to step away from the noise and type out a short, raw journal entry explaining exactly what is weighing on their mind or driving them to scroll in that moment.
* **Gloo AI + YouVersion Tool Calling**: This is where our custom backend takes over. We integrated the Gloo AI Studio Responses API to act as an empathetic spiritual guide. Gloo instantly analyzes the user's emotional state from their journal entry. Using native Tool Calling, Gloo autonomously reaches into the YouVersion Bible API to fetch a hyper-relevant, perfectly timed Bible verse tailored to their exact emotional need.
* **The Sacred Pause**: Selah doesn't just hand the user a verse. It enforces a 5-second, buttonless "Sacred Pause." No exits. No scrolling. Just the user, taking a breath, and anchoring their mind on the truth of Scripture.
* **Data Analytics & Behavioral Clustering**: To ensure long-term habit building, Selah’s backend clusters historical journal entries to reveal the user's "Top Patterns," showing them the root causes of their scrolling (e.g., Stress vs. Boredom).
* **The Armor of God Gamification**: As users build streaks of intentional pauses, they unlock and equip pieces of the biblical "Armor of God" (Ephesians 6) within a dedicated, gamified dashboard.
* **Global Accessibility**: We integrated on-the-fly AI translation to instantly localize the app interface and generated reflections, ensuring users can experience Selah in their native languages.

*Selah uses AI not to capture attention, but to reclaim it—turning our moments of deepest vulnerability into moments of profound grace.*

---

## 🛠️ How to Download and Install

Because Selah is an experimental app that actively interrupts your screen time, it relies on Android's **Accessibility Services**. When you install an app from outside the Google Play Store, newer Android versions (Android 13+) temporarily block these permissions for your security. 

Here is exactly how to install the app and allow it to work on your phone:

### Step 1: Download the App
1. Go to the **[Releases](https://github.com/)** page on this GitHub repository.
2. Download the latest `Selah-v4.7.0.apk` file to your Android phone.
3. Tap on the downloaded file and select **Install** (if your phone asks if you want to install apps from unknown sources, tap "Allow").

### Step 2: Unblock Restricted Settings (Android 13+)
Once the app is installed, you need to unlock its permissions.
1. Open your phone's **Settings** app.
2. Go to **Apps** and find **Selah** in the list.
3. Tap on **Selah** to open its App Info page.
4. **On Samsung / Google Pixel:** Look in the **top-right corner** of the screen for three vertical dots (`⋮`). Tap those dots and select **"Allow restricted settings"**.
5. Authenticate with your fingerprint or PIN if prompted. *(If you don't see the three dots, scroll to the bottom of the page and look for a setting that says "Allow restricted settings").*

### Step 3: Enable the Accessibility Shield
1. Open the **Selah** app from your home screen.
2. The app will prompt you to enable Accessibility. Tap the button to open your phone's Accessibility settings.
3. Navigate to **Installed Apps** (or "Downloaded Apps").
4. Tap on **Selah** and turn the switch to **ON**.
5. You're done! Head back to the Selah app, pick the apps you want to guard (like YouTube or TikTok), and reclaim your digital sanctuary.
