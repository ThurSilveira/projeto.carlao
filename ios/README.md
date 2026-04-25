# EscalaMinisterial — iOS

SwiftUI iOS 16+ application that mirrors the Android Escala Ministerial app.

## Requirements

- Xcode 15+
- iOS 16+ simulator or device
- The Spring Boot backend running at `http://localhost:8080/api`

## Architecture

- **Pattern**: MVVM with `ObservableObject` / `@StateObject`
- **Networking**: `URLSession` async/await (no external dependencies)
- **Navigation**: `TabView` root + `NavigationStack` per tab
- **No CocoaPods** — Swift Package Manager only

## Features

| Tab | Description |
|-----|-------------|
| Dashboard | Summary stats (ministers, events, schedules, feedback) |
| Ministros | CRUD, search, active filter, indisponibilidades calendar |
| Eventos | CRUD, search, type filter, cancel |
| Escalas | Status filter chips, generate/approve/cancel/delete |
| Feedback | List with ratings, respond to pending |
| Auditoria | Audit log with entity filter |

## Running

Open `Package.swift` in Xcode (or `xed ios/`) and run the `EscalaMinisterial` scheme on a simulator.

> Make sure to allow App Transport Security for `localhost` by adding the correct entry to the Info.plist if prompted. Xcode handles this automatically for `localhost` in debug builds.
