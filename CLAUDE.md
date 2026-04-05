# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

> Omar's working context for Claude Code sessions.
> This file is the single source of truth Claude reads at session start.

---

## Table of Contents

1. [Project Identity](#1-project-identity)
2. [Team & Domain Ownership](#2-team--domain-ownership)
3. [Build & Run Commands](#3-build--run-commands)
4. [Full Project Structure](#4-full-project-structure)
5. [Omar's Ownership](#5-omars-ownership)
6. [Realm Data Model](#6-realm-data-model)
7. [Gradle / Kotlin DSL Reference](#7-gradle--kotlin-dsl-reference)
8. [AndroidManifest Permissions](#8-androidmanifest-permissions)
9. [Intent Contracts](#9-intent-contracts-cross-team-api)
10. [Code Style & Conventions](#10-code-style--conventions)
11. [Build Order & Current Phase](#11-build-order--current-phase)
12. [Git Workflow](#12-git-workflow)
13. [Known Constraints & Gotchas](#13-known-constraints--gotchas)
14. [Skills Available to Claude](#14-skills-available-to-claude)
15. [Common Tasks & How Claude Should Handle Them](#15-common-tasks--how-claude-should-handle-them)
16. [Questions Claude Should Ask Before Writing Code](#16-questions-claude-should-ask-before-writing-code)
17. [Session Startup Checklist](#17-session-startup-checklist)
18. [Reference Files](#18-reference-files)

---

## 1. Project Identity

| Field | Value |
|---|---|
| **App name** | Campus Lost & Found |
| **Platform** | Android (native Java) |
| **IDE** | Android Studio (latest stable) |
| **Min SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 35 |
| **Build system** | Gradle — **Kotlin DSL only** (`.kts` files, zero Groovy) |
| **Database** | Realm (local, embedded) — `io.realm.kotlin:library-base:1.16.0` |
| **Architecture** | MVC — Activities = controllers, RealmObjects = models, XML = views |
| **VCS** | Git + GitHub (SSH). Feature-branch workflow. Never push directly to `main`. |
| **Package** | `com.uir.lostfound` |
| **Java version** | 11 |
| **My branch** | `feature/auth-session` (Omar's domain) |

---

## 2. Team & Domain Ownership

| Person | Domain | Branch |
|---|---|---|
| **Omar** (me) | Auth · Session · App Shell · Status Flow (Bonus 3) | `feature/auth-session` |
| **Idriss** | Item Feed · RecyclerView · Search/Filter · My Posts | `feature/feed-realm` |
| **Ines** | Post Item Form · Camera Intent · Photo Attachment (Bonus 1) | `feature/post-item` |
| **Mona** | Item Detail · Fragment · Claim Dialog · Notifications (Bonus 2) | `feature/detail-notify` |

**Merge discipline:** Open a PR → at least 1 teammate reviews → merge only when APK compiles clean.

---

## 3. Build & Run Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug

# Run local unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.uir.lostfound.ExampleUnitTest"

# Run instrumented tests (requires connected device or emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Full lint check
./gradlew lint
```

---

## 4. Full Project Structure

```
CampusLostFound/
├── app/
│   ├── build.gradle.kts                        # App-level build — Kotlin DSL
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/uir/lostfound/
│       │   ├── LoginActivity.java              [Omar]
│       │   ├── ItemFeedActivity.java           [Idriss]
│       │   ├── PostItemActivity.java           [Ines]
│       │   ├── ItemDetailActivity.java         [Mona]
│       │   ├── MyPostsActivity.java            [Idriss]
│       │   ├── ClaimNotificationReceiver.java  [Mona]
│       │   ├── adapter/
│       │   │   ├── ItemFeedAdapter.java        [Idriss]
│       │   │   └── MyPostsAdapter.java         [Idriss]
│       │   ├── fragment/
│       │   │   └── ItemDetailFragment.java     [Mona]
│       │   ├── model/
│       │   │   └── LostItem.java              [Idriss] ← Realm RealmObject
│       │   ├── db/
│       │   │   └── RealmHelper.java           [Idriss] ← singleton CRUD
│       │   └── utils/
│       │       ├── SessionManager.java        [Omar]
│       │       ├── StatusUtils.java           [Omar]
│       │       ├── ImageHelper.java           [Ines]
│       │       └── NotificationHelper.java    [Mona]
│       └── res/
│           ├── layout/
│           │   ├── activity_login.xml         [Omar]
│           │   ├── activity_item_feed.xml     [Idriss]
│           │   ├── activity_post_item.xml     [Ines]
│           │   ├── activity_item_detail.xml   [Mona]
│           │   ├── activity_my_posts.xml      [Idriss]
│           │   ├── item_card.xml              [Idriss]
│           │   ├── item_my_post_card.xml      [Idriss]
│           │   ├── fragment_item_detail.xml   [Mona]
│           │   └── dialog_claim_confirm.xml   [Mona]
│           ├── menu/
│           │   └── menu_feed.xml              [Omar]
│           ├── values/
│           │   ├── strings.xml               [All — merge via PR]
│           │   ├── colors.xml                [All] — primary navy #1E3A8A, secondary green #10B981
│           │   └── styles.xml                [Omar seeds, all extend] — status chip styles
│           └── xml/
│               └── file_paths.xml            [Ines — FileProvider config]
├── build.gradle.kts                           # Project-level — Kotlin DSL
├── settings.gradle.kts                        # Realm classpath
├── gradle/
│   └── libs.versions.toml                     # Version catalog (preferred)
└── .gitignore
```

The app uses a single `MainActivity` as the initial scaffold (Phase 1 skeleton); `LoginActivity` replaces it as the LAUNCHER activity once auth is wired in.

---

## 5. Omar's Ownership

Everything in this section is **mine to build and own**. Claude should focus here when I ask for code.

### 5.1 Files I Own

```
app/src/main/java/com/uir/lostfound/
├── LoginActivity.java                         # Auth entry point
├── utils/
│   ├── SessionManager.java                    # SharedPreferences wrapper
│   └── StatusUtils.java                       # Status constants + chip colours
app/src/main/res/
├── layout/
│   └── activity_login.xml                     # Login screen layout
└── values/
    └── styles.xml                             # Status chip styles (shared by all adapters)
app/build.gradle.kts                           # Realm plugin, all deps — I configure this
settings.gradle.kts                            # Realm classpath declaration
app/src/main/AndroidManifest.xml               # Permissions, activities (all team edits via PR)
database/seeders/ (README seed instructions)   # Demo data instructions
```

### 5.2 SessionManager Contract

```java
// Writes
SessionManager.saveUser(Context, String studentId, String name)
// Reads
String SessionManager.getStudentId(Context)
String SessionManager.getName(Context)
boolean SessionManager.isLoggedIn(Context)
// Clears
SessionManager.clear(Context)
```

Backed by `SharedPreferences` named `"uir_session"`. Keys: `"student_id"`, `"name"`. No external library. `isLoggedIn()` returns `!getStudentId(ctx).isEmpty()`.

### 5.3 StatusUtils Contract

```java
// Constants
StatusUtils.OPEN       = "OPEN"
StatusUtils.CLAIMED    = "CLAIMED"
StatusUtils.RETURNED   = "RETURNED"

// Returns a hex colour string for the chip background
String StatusUtils.getChipColor(String status)
// OPEN     → "#22C55E"  (green)
// CLAIMED  → "#F59E0B"  (amber)
// RETURNED → "#94A3B8"  (slate/grey)
// unknown  → "#E5E7EB"  (fallback light grey)

// Returns a human-readable label
String StatusUtils.getLabel(String status)
```

Pure static utility class — no constructor, no state. Must handle null input gracefully (return fallback colour).

### 5.4 ActionBar Menu (ItemFeedActivity — shared, I implement)

I wire the ActionBar menu in `ItemFeedActivity` (Idriss owns the Activity but I set up the menu infrastructure):

- **Filter:** Lost / Found / All (toggle via `onOptionsItemSelected`)
- **Sort:** Newest first / Oldest first
- **My Reports** → launches `MyPostsActivity`

Menu XML lives at `res/menu/menu_feed.xml` — I create it, Idriss integrates it.

### 5.5 Status Flow Lifecycle (Bonus 3)

```
OPEN  ──(finder taps "Mark as Claimed")──►  CLAIMED  ──(owner taps "Confirm Returned")──►  RETURNED
```

- Status chip is rendered in **both** `ItemFeedAdapter` (Idriss) and `ItemDetailFragment` (Mona) using `StatusUtils.getChipColor()` — I define the helper, they call it.
- On transition to `CLAIMED`: fire AlarmManager → `ClaimNotificationReceiver` (Mona owns the receiver, I wire the trigger from `StatusUtils` or a shared `StatusTransitionHelper`).

---

## 6. Realm Data Model

**Single model: `LostItem.java`** (owned by Idriss — I reference it, never edit it without a PR).

```java
public class LostItem extends RealmObject {
    @PrimaryKey
    private String id;           // UUID string
    private String title;
    private String description;
    private String location;
    private String category;     // "Electronics"|"Documents"|"Clothing"|"Bags"|"Keys"|"Other"
    private String type;         // "LOST" | "FOUND"
    private String status;       // "OPEN" | "CLAIMED" | "RETURNED"  ← StatusUtils constants
    private String photoPath;    // nullable — local file path
    private long timestamp;      // System.currentTimeMillis()
    private String ownerStudentId;
    private String ownerName;
}
```

**RealmHelper** (Idriss) exposes:

```java
RealmHelper.getInstance()
insertItem(LostItem)
getAllItems()                         // RealmResults<LostItem>
getItemById(String id)
getItemsByOwner(String studentId)
updateItemStatus(String id, String newStatus)
deleteItem(String id)
updateItem(String id, LostItem updated)
```

---

## 7. Gradle / Kotlin DSL Reference

### settings.gradle.kts

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("io.realm.kotlin") version "1.16.0" apply false
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "CampusLostFound"
include(":app")
```

### app/build.gradle.kts

```kotlin
plugins {
    id("com.android.application")
    id("io.realm.kotlin")          // No apply false here — actually apply it
}

android {
    namespace = "com.uir.lostfound"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.uir.lostfound"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation("io.realm.kotlin:library-base:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.core:core-ktx:1.13.1")
}
```

> **Note:** `gradle/libs.versions.toml` (the version catalog) may list newer artifact versions (e.g. appcompat 1.7.1, material 1.13.0). The `build.gradle.kts` snippet above is the canonical reference; update the catalog entries to match when bumping versions.

### Key Kotlin DSL rules (never forget)

| Groovy | Kotlin DSL |
|---|---|
| `compileSdkVersion 35` | `compileSdk = 35` |
| `minSdkVersion 24` | `minSdk = 24` |
| `implementation 'group:art:ver'` | `implementation("group:art:ver")` |
| `apply plugin: 'x'` | `id("x")` inside `plugins {}` |
| `minifyEnabled false` | `isMinifyEnabled = false` |
| `buildConfigField "String", "K", "V"` | `buildConfigField("String", "K", "V")` |

---

## 8. AndroidManifest Permissions

```xml
<!-- Omar adds these in Phase 1 -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />  <!-- API 33+ -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />   <!-- API 33+ -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />                                          <!-- below API 33 -->
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" /><!-- AlarmManager -->

<!-- Activities (all four registered here) -->
<activity android:name=".LoginActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN"/>
        <category android:name="android.intent.category.LAUNCHER"/>
    </intent-filter>
</activity>
<activity android:name=".ItemFeedActivity" android:exported="false"/>
<activity android:name=".PostItemActivity" android:exported="false"/>
<activity android:name=".ItemDetailActivity" android:exported="false"/>
<activity android:name=".MyPostsActivity" android:exported="false"/>

<!-- Mona registers this -->
<receiver android:name=".ClaimNotificationReceiver" android:exported="false"/>

<!-- Ines registers this -->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths"/>
</provider>
```

`INTERNET` is also present (commented out) in the current skeleton — uncomment when adding any network features.

---

## 9. Intent Contracts (Cross-Team API)

These are the **agreed Intent extras** the team uses to pass data between Activities. Treat them as an API contract — don't change keys without telling the team.

```java
// ItemFeedActivity → ItemDetailActivity
intent.putExtra("ITEM_ID", item.getId());       // String

// ItemFeedActivity → PostItemActivity (new)
// No extras needed — blank form

// MyPostsActivity → PostItemActivity (edit)
intent.putExtra("ITEM_ID", item.getId());        // String — edit mode
intent.putExtra("EDIT_MODE", true);              // boolean

// LoginActivity → ItemFeedActivity
// No extras — session read from SessionManager
```

---

## 10. Code Style & Conventions

- **Language:** Java only — no Kotlin in `src/main/java/`. Kotlin DSL is **build files only**.
- **Naming:** `PascalCase` for classes, `camelCase` for methods/fields, `UPPER_SNAKE_CASE` for constants.
- **No anonymous inner classes** for click listeners if the handler is more than 3 lines — use a named method or lambda.
- **No hardcoded strings** in Java — all user-visible strings go in `res/values/strings.xml`.
- **No hardcoded colours** in XML layouts — reference `@color/` from `colors.xml`.
- **Realm rules:**
  - All Realm writes inside `realm.executeTransaction()`.
  - Never pass a managed `RealmObject` between threads — copy it out first with `realm.copyFromRealm()`.
  - Close Realm instances that are opened manually (use try-with-resources or lifecycle-aware pattern).
- **Null safety:** Always null-check `Intent` extras before using them.
- **ViewHolder pattern** mandatory in all `RecyclerView.Adapter` subclasses.

---

## 11. Build Order & Current Phase

```
Phase 1 (Day 1)   ← Omar's primary focus
  [x] Android Studio project created (Kotlin DSL)
  [x] Realm configured in .kts files
  [x] Package structure created
  [x] Manifest: permissions + all activities registered
  [x] SessionManager.java implemented
  [x] StatusUtils.java implemented
  [x] LoginActivity.java + activity_login.xml
  [x] menu_feed.xml + ActionBar menu wiring
  [x] styles.xml with status chip styles
  [x] Initial push to GitHub, README

Phase 2 (Parallel with team)
  [ ] Test LoginActivity end-to-end on emulator
  [ ] Confirm SessionManager SharedPreferences persists across Activity restarts
  [ ] Help Idriss wire menu_feed.xml into ItemFeedActivity

Phase 3
  [ ] Status chip rendering confirmed working in ItemFeedAdapter (Idriss calls StatusUtils)
  [ ] StatusTransitionHelper.java — shared helper to fire status change + trigger AlarmManager

Phase 4
  [ ] Admin dashboard shell (summary cards + LowStockWidget embed placeholder)
  [ ] Wire StatusTransitionHelper into Mona's ItemDetailActivity trigger

Phase 5 (Integration)
  [ ] Smoke-test full flow: login → browse → post → view → claim → notification → my posts → delete
  [ ] Fix merge conflicts in AndroidManifest.xml (expected)

Phase 6 (Polish)
  [ ] Seed demo data (2 users, 8 lost + 4 found items across all categories)
  [ ] UML class diagram for report
  [ ] Final APK build + test API 30 + API 34 emulators
```

---

## 12. Git Workflow

```bash
# START of every session
git checkout main
git pull origin main
git checkout -b feature/auth-session

# During work
git status
git add .
git commit -m "feat(auth): implement SessionManager with SharedPreferences"

# Before pushing — always rebase on latest main first
git checkout main && git pull origin main
git checkout feature/auth-session
git merge main                      # resolve any conflicts here
git push -u origin feature/auth-session

# After PR merged
git checkout main
git pull origin main
git branch -d feature/auth-session
```

### Commit message convention

```
feat(auth): add LoginActivity validation
feat(status): implement StatusUtils chip colours
fix(session): clear session on logout
chore: register all activities in Manifest
refactor(status): extract StatusTransitionHelper
docs: update CLAUDE.md with Phase 2 checklist
```

---

## 13. Known Constraints & Gotchas

| Constraint | Detail |
|---|---|
| **Realm + Java** | Use `realm.executeTransaction()` for all writes. Managed objects cannot cross threads. |
| **Camera URI (API 24+)** | Must use `FileProvider.getUriForFile()` — direct `file://` URIs are blocked. Ines owns this, but if I touch PostItemActivity I must respect it. |
| **POST_NOTIFICATIONS (API 33+)** | Must request at runtime — not just in Manifest. Add runtime permission request in the Activity that triggers the first notification. |
| **AlarmManager exact alarms (API 31+)** | Requires `SCHEDULE_EXACT_ALARM` permission AND a runtime check with `alarmManager.canScheduleExactAlarms()`. |
| **Realm + Kotlin DSL** | Use `io.realm.kotlin` (Realm Kotlin SDK) plugin, not the legacy `io.realm` Groovy plugin. They have different APIs. |
| **RecyclerView scroll state** | If Idriss reports RecyclerView losing scroll position after filter, the fix is to call `adapter.notifyDataSetChanged()` only when the dataset actually changes, or use `DiffUtil`. |
| **Back stack** | `LoginActivity` must call `finish()` after launching `ItemFeedActivity`. Otherwise the back button goes back to login. |
| **Realm migration** | If the `LostItem` schema changes after first run, a `RealmMigration` must be provided or `deleteRealmIfMigrationNeeded()` used during development. |
| **Kotlin DSL first sync** | Initial Gradle sync with Kotlin DSL is slower than Groovy. Normal — don't switch back. |

---

## 14. Skills Available to Claude

The following skills are installed and Claude should use them proactively:

### Architecture & Diagrams
- **`/home/oghyd/Desktop/Bureau/uni/CLAUDE_SKILLS_UNZIPPED_NON_EXHAUSTIVE/uml-architect/SKILL.md`** — Use whenever I ask for a class diagram, sequence diagram, state machine, or any UML. Follows a structured reasoning process: context → candidate interpretations → diagram selection → render.
- **`/home/oghyd/Desktop/Bureau/uni/CLAUDE_SKILLS_UNZIPPED_NON_EXHAUSTIVE/plant-uml/SKILL.md`** — Generates PlantUML `.puml` files and renders them to PNG/SVG via Java. Use for all diagram rendering. Falls back to inline SVG via visualizer if Java unavailable.

### Documentation
- **`/home/oghyd/Desktop/Bureau/uni/CLAUDE_SKILLS_UNZIPPED_NON_EXHAUSTIVE/pdf-skill/SKILL.md`** — Read, create, merge, or extract from PDF files. Use for reading the original `CampusLostFound_ProjectPlan` stored in `/home/oghyd/Desktop/Bureau/uni/3eme_annee/S6/DEV_MOBILE/Project/CampusLostFound_ProjectPlan.pdf` if needed mid-session.

### Visualisation & Frontend
- **`/home/oghyd/Desktop/Bureau/uni/CLAUDE_SKILLS_UNZIPPED_NON_EXHAUSTIVE/frontend-design/SKILL.md`** — High-quality UI mockup or web component. Useful for generating HTML wireframes of the Android screens before implementing XML layouts.
- **`/home/oghyd/Desktop/Bureau/uni/CLAUDE_SKILLS_UNZIPPED_NON_EXHAUSTIVE/ui-ux-pro-max-skill/`** — This one is different, this is not just a skill but a set of skills with its own CLAUDE.md (Read it carefully), read the zip thoroughly and use it when creating UI/UX components. (Has higher priority than the frontend design skill).

### Quick Reference

| Task I ask for | Skill Claude should use |
|---|---|
| "Draw me the class diagram for this project" | `uml-architect` → `plantuml` |
| "Generate the PlantUML for the status state machine" | `plant-uml` |
| "Wireframe the Login screen" | `ui-ux-pro-max-skill` |
| "Read this PDF and extract the grading rubric" | `pdf-skill` |

---

## 15. Common Tasks & How Claude Should Handle Them

### "Write LoginActivity"
1. Use `SessionManager` as defined in §5.2.
2. Validate: student ID must be non-empty, name must be non-empty.
3. On success: call `SessionManager.saveUser()`, start `ItemFeedActivity`, call `finish()` so back doesn't return to login.
4. No password — this is a campus internal tool, student ID + name only.

### "Write SessionManager"
- Use `Context.getSharedPreferences("uir_session", Context.MODE_PRIVATE)`.
- Keys: `"student_id"`, `"name"`.
- `isLoggedIn()` returns `!getStudentId(ctx).isEmpty()`.
- `clear()` calls `editor.clear().apply()`.

### "Write StatusUtils"
- Pure static utility class — no constructor, no state.
- All status values as `public static final String` constants.
- `getChipColor(String status)` returns hex strings (see §5.3).
- Must handle null input gracefully (return fallback colour).

### "Set up Gradle with Realm"
- Follow the Kotlin DSL snippets in §7 exactly.
- Realm version to use: check https://github.com/realm/realm-kotlin/releases for latest stable before hardcoding.
- Never use `apply plugin:` syntax — that's Groovy. Always use `plugins { id("...") }`.

### "Generate the UML class diagram"
1. Invoke `uml-architect` skill.
2. Include all 5 Java model/util classes + 5 Activities + 2 Adapters + 1 Fragment + RealmHelper.
3. Show relationships: Activity → uses → SessionManager, Activity → uses → RealmHelper, Adapter → uses → StatusUtils, etc.
4. Use `plantuml` skill to render it.
5. Save the `.puml` file to `/home/claude/diagrams/class_diagram.puml`.

### "Generate state machine for item status"
1. Invoke `uml-architect` skill.
2. Three states: OPEN, CLAIMED, RETURNED.
3. Transitions: OPEN → CLAIMED (event: finderMarksClaimed), CLAIMED → RETURNED (event: ownerConfirmsReturned).
4. Render with `plantuml`.

### "Write the soutenance report"
1. Invoke `latex-report-generator` skill.
2. Sections: Introduction, Architecture, Feature Walkthrough (per domain), UML Diagrams, Bonus Features, Team Contribution, Conclusion.
3. Embed the PlantUML-generated diagrams as images.
4. Run `humanizer` skill on prose sections before final output.

### "Seed demo data"
Provide a helper method or instructions to manually insert via RealmHelper:
- 2 users: `{ "STU001", "Omar" }` and `{ "STU002", "Fatima" }`
- 4 LOST items (Electronics, Documents, Bags, Keys) — status OPEN
- 4 LOST items — status CLAIMED or RETURNED
- 4 FOUND items — status OPEN

---

## 16. Questions Claude Should Ask Before Writing Code

If I give an ambiguous request, Claude should ask **one focused question** before proceeding:

- "Should `LoginActivity` skip to `ItemFeedActivity` automatically if a session already exists, or always show the login form?" → **Expected answer: yes, skip if already logged in.**
- "Should `StatusUtils.getChipColor()` return a hex string or an Android colour resource ID?" → **Expected answer: hex string — simpler to use in both Java and XML.**
- "Should demo seed data be inserted via a button in a dev-only screen, or via a `RealmHelper.seedDemoData()` call in `Application.onCreate()` guarded by `BuildConfig.DEBUG`?" → **Expected answer: `BuildConfig.DEBUG` guard in Application class.**

If the answer is already obvious from this file, **don't ask — proceed with the documented decision.**

---

## 17. Session Startup Checklist

When Claude Code starts a new session on this project, do this in order:

1. Read this `CLAUDE.md` fully.
2. Check which phase we're on (§11) — ask me to update it if it looks stale.
3. If I ask for diagrams → load `uml-architect` + `plantuml` skills.
4. If I ask for a report/document → load `docx` or `latex-report-generator` skill.
5. If I ask to read an uploaded PDF → load `pdf-reading` skill.
6. Never generate Groovy syntax in `.gradle` files — always Kotlin DSL.
7. Never generate Kotlin in `src/main/java/` — always Java.
8. Default branch for my work: `feature/auth-session`.

---

## 18. Reference Files

| File | What it is |
|---|---|
| `CampusLostFound_ProjectPlan.docx` | Our generated plan — the canonical spec |
