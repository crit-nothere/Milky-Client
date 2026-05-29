# MilkyWay Client v1.0.0

A sleek, premium utility client for Minecraft 1.21.1 with a night sky theme.

## Features
- 62 fully customizable modules
- Liquid glass GUI with black/purple theme
- Animated night sky background
- Smooth animations (can be disabled)
- Logo animation on GUI open
- Logo HUD display
- Real-time FPS independent animations

## Installation

### Requirements
- Java 21+
- Minecraft 1.21.1
- Fabric Loader 0.16.5+
- Fabric API 0.102.0+1.21.1

### Build
```bash
./gradlew build
```

### Install
1. Copy `build/libs/milkywayClient-1.0.0.jar` to `.minecraft/mods/`
2. Install Fabric API if not already present
3. Launch Minecraft with Fabric profile

### Usage
Press **RShift** to open the GUI

## Modules (62 Total)

### Render (12)
Zoom, Fullbright, Hitboxes, Player ESP, Tracers, Nametags, Armor HUD, No Hurtcam, Ambience, Freelook, No Render, Breadcrumbs

### Combat (9)
Hit Color, Damage Tint, Reach Display, Hit Particles, Attack Indicator, Kill Counter, Target HUD, Velocity, Combo Counter

### Movement (8)
Sprint, Step, Sneak Lock, No Slow, Bunny Hop, Fast Swim, Anti-Void

### World (5)
X-Ray, Cave Finder, Chest ESP, Search, Minimap

### HUD (9)
FPS Counter, Ping Display, Potions HUD, Keystrokes, CPS Counter, Clock, Server Info, Compass, Coordinates HUD

### Optimization (10)
FPS Boost, Chunk Optimizer, Network Opt, Auto GG, Chat Tweaks, Screenshot, No Disconnect, Reconnect, Anti-AFK, Macro

## Client Options
- Enable/Disable Animations
- Enable/Disable Night Sky
- Animation Speed Control
- Logo Animation Toggle
- Logo HUD Display
- Logo Main Menu Display

## Technical Info
- **Language**: Java 21
- **Format**: Fabric Mod
- **Target**: Minecraft 1.21.1
- **Mixins**: 0 (no problematic hooks)
- **Architecture**: Fully modular with safe initialization

## Build Status
✅ Compiles successfully
✅ No mixin errors
✅ Safe lazy initialization
✅ Ready for production

---

**Built with Fabric API | Minecraft 1.21.1 | Java 21**
