# AUNIO.AI — User Interface Design & Architecture

This document defines the visual design system, navigation hierarchy, interactive states, and modular codebase structure for the **AUNIO.AI Personal Companion** interface. Built with **Jetpack Compose** and strictly adhering to **Material Design 3 (M3)**, this design prioritizes a highly polished, responsive, and distraction-free user experience.

---

## 1. Visual Direction & Design System

AUNIO.AI adopts a **Cosmic Minimalist Dark Theme** ("Aunio Dark") to convey safety, calm focus, and premium intelligence. The interface leverages generous negative space, subtle surface elevations, and sharp neon indicators.

### A. Color Palette (Tokens)
| Token | Hex Value | Purpose |
| :--- | :--- | :--- |
| `Background` | `#0A0B0E` | Pure deep slate-black base; reduces eye strain and fits OLED screens. |
| `Surface` | `#12141C` | Elevated containers, cards, and modal sheets. |
| `Primary` | `#4F46E5` | Indigo Blue; primary actions, brand presence, active highlights. |
| `Accent` | `#06B6D4` | Cyan; AI-specific highlights, draft suggestions, sparks. |
| `Success` | `#10B981` | Emerald Green; completed goals, active backup status indicators. |
| `OnBackground` | `#F3F4F6` | High-contrast off-white for crisp, readable text. |
| `OnSurfaceVariant` | `#9CA3AF` | Neutral-gray for secondary text, metadata labels, and borders. |

### B. Typography Pairings
*   **Headings / Display:** Custom display-scale typography utilizing geometric sans-serif fonts (e.g., *Space Grotesk* or clean M3 *Sans*) with generous tracking (letter spacing) and wide weights.
*   **Body & Chat Bubbles:** High-legibility humanist system fonts with custom line-height scaling (`1.5` times text size) to prevent reader fatigue.
*   **Technical / Status Metadata:** Monospaced type (*JetBrains Mono*) at micro-scales (`11sp` to `12sp`) with subtle opacity for timestamp readouts and database states.

---

## 2. Navigation Hierarchy & App Shell

The interface operates on an adaptive, responsive **Single-Activity, Multi-Section App Shell**. On mobile devices, navigation uses an ergonomic bottom navigation bar, while wide-screen layouts automatically switch to a left-anchored navigation rail.

### Custom Responsive Layout Matrix:
```
┌────────────────────────────────────────────────────────┐
│                        APP BAR                         │
├────────────────────────────────────────────────────────┤
│                                                        │
│                                                        │
│                     SCREEN CONTENT                     │
│               (Chat, Goals, Projects, etc.)           │
│                                                        │
│                                                        │
├────────────────────────────────────────────────────────┤
│                  BOTTOM NAVIGATION BAR                 │
└────────────────────────────────────────────────────────┘
```

*   **Primary Sections (5 Tabs):**
    1.  **Chat:** Ephemeral natural language playground, hosting the active AI conversation and floating suggestion/clarification cards.
    2.  **Goals:** Milestones organizer containing active, paused, completed, and archived milestones.
    3.  **Projects:** Parent initiative boards coordinating related child milestones.
    4.  **Memory:** Fact Vault showing extracted concepts categorized into Preferences, Goals, Relationships, and Facts.
    5.  **Settings:** Locale switching (English/Masri Arabic), biometric locks, voice profiles, and backup options.

---

## 3. Section-by-Section Interface Designs

### Section 1: Chat (The Conversation Workspace)
*   **Layout:** Vertical scrollable timeline of conversation bubbles with dynamic message entrance transitions (slide-up and fade-in).
*   **Bilingual Directionality:** Natively supports bi-directional styling. English displays left-aligned (LTR); Arabic displays right-aligned (RTL).
*   **The AI Suggestion Host:** Interstitially inserts interactive proposal cards (e.g., Goal Draft Proposals, Low Confidence Memory Queries) directly into the stream, maintaining user focus.
*   **Aesthetic Details:** Message input field features a glowing neon accent border and a clean, accessibility-optimized send button (minimum size: `48dp`).

### Section 2: Goals (The Personal Planner)
*   **Layout:** Tabbed horizontal sub-navigation dividing milestones into **Active**, **Completed**, **Paused**, and **Archived**.
*   **List Items:** Layered cards with a trailing checkbox to toggle status. Checking a goal triggers a delightful spring particle effect and moves the item to the Completed tab.
*   **Empty State:** Displays a custom visual illustration (e.g., a quiet mountain peak) accompanied by a cyan button prompt: *"Ask Aunio to draft a goal for you."*

### Section 3: Projects (Strategic Initiatives)
*   **Layout:** Clean grid layout of large cards displaying parent projects.
*   **Card Attributes:**
    *   Project name & high-level description.
    *   Linear progress indicator (custom colored track representing completed vs total child goals).
    *   Status badge in the top-right corner (`ACTIVE` in indigo, `PAUSED` in amber, `ARCHIVED` in gray).
*   **Action Drawer:** Long-pressing a card opens a contextual action sheet from the bottom, enabling the user to edit details, pause, complete, or archive the project.

### Section 4: Memory (The Cognitive Vault)
*   **Layout:** Organized into 4 visual quadrants corresponding to memory categories: **Preferences**, **Goals**, **Relationships**, and **Facts**.
*   **FTS5 Search Utility:** Features a prominent sticky search bar at the top with a magnifying glass icon. Typing performs instantaneous database search index lookups, filtering cards dynamically.
*   **Direct Modification Rules:** In line with privacy principles, each memory chip includes a subtle delete trashcan button, giving the user absolute authority to remove any stored personal fact.

### Section 5: Settings (Control & Security Center)
*   **Layout:** Grouped vertical list of interactive toggle cards.
*   **Control Groups:**
    *   **Bilingual Toggle:** Simple segment switcher between English & Egyptian Arabic.
    *   **Theme Settings:** Dark theme / Light theme toggle.
    *   **Biometric Security:** Toggle to lock the application using local device fingerprint scanners.
    *   **Backup & Passphrase:** Local backup generator. Displays historical backup meta-logs with green success badges.

---

## 4. Code Modularization Strategy (Avoiding Giant Files)

To keep the codebase maintainable, fast-compiling, and clean, the current monolithic `AunioMainScreen.kt` file is refactored into a highly modular directory structure:

```
app/src/main/java/com/example/ui/
│
├── AunioMainActivity.kt            # Entrypoint & window-insets config
├── AunioViewModel.kt               # Central business state engine
│
├── components/                     # Reusable across multiple screens
│   ├── CommonButtons.kt            # Standard styled filled, outlined, & icon buttons
│   ├── CommonCards.kt              # Standard layered surface containers
│   ├── NavigationShell.kt          # Bottom bar & navigation rail layouts
│   └── SuggestionCard.kt           # Ephemeral proposal card with actions
│
├── chat/                           # Chat-specific views
│   ├── ChatScreen.kt               # Core Chat viewport container
│   ├── ChatBubble.kt               # Individual message bubbles (supports LTR/RTL)
│   └── InputBar.kt                 # Action input field & attachments row
│
├── goals/                          # Goals-specific views
│   ├── GoalsScreen.kt              # Core Goals viewport with state tabs
│   └── GoalListItem.kt             # Individual goal card with checkbox & status toggles
│
├── projects/                       # Projects-specific views
│   ├── ProjectsScreen.kt           # Core Projects grid view
│   └── ProjectGridCard.kt          # Strategic project representation card
│
├── memory/                         # Memory-specific views
│   ├── MemoryScreen.kt             # Cognitive Vault screen with category tabs
│   ├── MemorySearchBar.kt          # Interactive query lookup field
│   └── MemoryConceptChip.kt        # Card chips representing extracted facts
│
└── settings/                       # Settings-specific views
    ├── SettingsScreen.kt           # System configuration viewport
    └── SettingsToggleItem.kt       # Reusable toggle rows with icons
```

---

## 5. Performance and Accessibility Mandates

1. **Recomposition Optimization:** Reusable components are marked as `@Stable` or `@Immutable`. Lists utilize `@Composable` keys (e.g., `LazyColumn(items(..., key = { it.id }))`) to prevent redundant renders during chat streaming.
2. **Strict Safe-Drawing (No Camera Cutouts):** Root screens utilize standard Jetpack `Scaffold` containers that directly consume `WindowInsets.systemBars` or `WindowInsets.safeDrawing`, preventing notch overlapping.
3. **Ergonomic Clickable Targets:** Every interactable component (icon buttons, tab indicators, checkboxes) has a layout size of at least `48.dp` or includes dynamic padding using Material 3 `minimumInteractiveComponentSize()`.
4. **Interactive Feedback:** No touch action is static; every action features localized M3 ripples or state transformation animations to indicate active system recognition.
