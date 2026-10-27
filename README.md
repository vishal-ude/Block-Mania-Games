# Block Mania — Premium Competitive Block Puzzle Game

A polished, competitive, and responsive block puzzle game for Android built with **Kotlin** and **Jetpack Compose**.

---

## 🌟 Key Features

### 1. 8×8 Puzzle Board & Tactile Drag-and-Drop
- **High-Sensitivity Dragging**: Low-latency pointer input with 3dp activation threshold. Zero drag lag.
- **Cell-Relative Snapping**: Smart grid snapping with 0.95f tolerance ensuring smooth and forgiving placement.
- **Authoritative Ghost Preview**: Placement preview that precisely matches final board validation.
- **Move Undo**: One-tap move undo functionality that safely restores the previous valid board state, tray piece, and score.
- **Row & Column Clearing**: Dynamic multi-line clearing with combo scoring multipliers.

### 2. Competitive Mode & Progression System
- **Daily Challenges**: Daily puzzle goals that automatically reset each calendar day with local persistent progress.
- **Progressive Missions**: In-game missions with live progress indicators.
- **Achievement Showcase**: Milestones for line clears, multi-clears, combos, and high scores.
- **Unlockable Themes**: Unlock vibrant game themes (Ocean Blue, Candy Pop, Emerald) through gameplay achievements and score milestones.
- **Career & Match Stats**: In-depth tracking of career games, total score, best records, and line clear statistics.

### 3. Original Visual Identity & UI
- Custom resolution-independent vector icons replacing generic emojis.
- Dynamic theme palettes with ambient gradients, rounded tiles, and soft shadows.
- Polished dialogs for Pause, Game Over, Competitive Hub, and Theme Selection.
- Pure black Android status bar (`#000000`) with light system icons and correct system-bar insets.

### 4. 10 Procedural Audio Cues
- Built-in lightweight 16-bit PCM procedural sound effects via `SoundPool`:
  - Block Pickup, Block Placement, Invalid Placement
  - Single Line Clear, Multi-Line Combo Clear
  - Score Milestone, New Personal Best
  - Button Click, Game Over, Daily Challenge Completion
- Manages audio focus, volume balance, and user mute preferences.

### 5. Save-and-Resume & Offline Play
- 100% offline gameplay core.
- Automatic delimiter-based state serialization preserving board, tray, and score across app restarts.
- Resume prompt on app launch allowing seamless pickup from the last active puzzle.

### 6. Policy-Compliant Rewarded Ad Architecture
- Pre-configured with Google Mobile Ads test ID (`ca-app-pub-3940256099942544/5224354917`).
- Strictly opt-in for continuing a game once per session with central 4×4 board revive.
- Disabled by default (`isMonetizationEnabled = false`) until production Google Play Families compliance is completed.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with unidirectional data flow
- **Audio Engine**: Android `SoundPool` with synthesized procedural PCM buffers
- **Persistence**: Android `SharedPreferences` with custom delimiter-based state serialization
- **Target SDK**: Android 14+ (API 34), Min SDK 24

---

## 🚀 Building & Running

### Prerequisites
- Android Studio Ladybug | 2024.2+ or Command Line Tools
- JDK 17+
- Android SDK Platform 34

### Run Unit Tests
```bash
./gradlew test
```

### Build Debug APK
```bash
./gradlew assembleDebug
```

---

## 📄 License
All assets, icons, sound designs, and code are original.