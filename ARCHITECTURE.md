# AUNIO.AI — Personal AI Companion
## Software Architecture Document (SAD)
**Version:** 1.1.0 (Refined & Approved)  
**Target Platform:** Android (Kotlin, Jetpack Compose)  
**Security Level:** Hardware-backed Keystore, Zero-Knowledge Local AES-256-GCM Encryption, SQLCipher-ready Offline Storage  
**Primary Languages:** English & Arabic (with colloquial Egyptian Arabic understood natively)

---

## 1. Executive Summary & Product Vision

**AUNIO.AI** is designed as a secure, high-utility, local-first **Personal AI Companion**. Rather than acting as a simple stateless chat client, AUNIO.AI operates as a proactive auxiliary mind for the user—combining episodic conversation history, cognitive long-term memory, an integrated goals/projects planner, smart local reminders, and secure backup controls. 

The application is engineered to grant **equal, first-class priority to English and Arabic languages**, natively understanding both Modern Standard Arabic (MSA) and colloquial **Egyptian Arabic (Masri)**. Privacy is the core architectural anchor: all primary user records, goals, and conversations are persisted offline in a local database with options for secure, client-side encrypted backup.

---

## 2. Key Architectural Principles

```
┌────────────────────────────────────────────────────────┐
│                      AUNIO.AI UI                       │
│        (Bilingual / Dynamic LTR-RTL M3 Compose)       │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│             Reactive State Management (VM)             │
│            (Kotlin Coroutines & StateFlows)            │
└──────┬───────────────────┬──────────────────────┬──────┘
       ▼                   ▼                      ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────────┐
│ Memory Engine│   │ AI Chat Core │   │ Goals & Projects │
└──────┬───────┘   └──────┬───────┘   └───────┬──────────┘
       │                  │                   │
       └───────────┐      ▼      ┌────────────┘
                   ▼             ▼
┌────────────────────────────────────────────────────────┐
│          Database Repository abstraction               │
│             (Room + SQLite FTS5 Search)                │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│               Security & Encryption Layer              │
│       (Android Keystore, AES-256-GCM, PBKDF2 Key Der)  │
└────────────────────────────────────────────────────────┘
```

1. **Local-First & Offline Resilience:** All core interactions (writing notes, viewing projects, creating goals, editing reminders) work instantly and completely offline. The local SQLite database is the single source of truth.
2. **First-Class Bilingualism:** RTL (Right-to-Left) layouts and Arabic language assets are built into the design foundation rather than retrofitted. The UI responds dynamically to language switching without restart.
3. **Colloquial (Egyptian Arabic) Native Understanding:** Built-in semantic parsers and contextual system prompts allow the AI core to decode dialectal idioms, Egyptian regionalisms, and "Franco-Arabic" (Latinized Arabic script) with native fluidity.
4. **Data Ownership & Cryptographic Privacy:** User memories and daily routines are highly sensitive. Backup files are encrypted locally on-device using AES-256-GCM before export, keeping the developer or cloud host entirely blind to content.
5. **Context-Assembled LLM Orchestration:** Gemini API interactions are driven by an on-device contextual synthesis pipeline. The prompt is assembled on-the-fly, pulling relevant active goals, active reminders, and historical semantic memories matching the current conversation.

---

## 3. Technology Stack Choice

* **Language:** Kotlin (100% type-safe, asynchronous concurrency via Coroutines & Flow).
* **UI Framework:** Jetpack Compose (Declarative, reactive layouts with native support for RTL directions, Material Design 3, dynamic typography scaling).
* **Local Persistence:** Room DB with KSP (Kotlin Symbol Processing) and **FTS5 (Full-Text Search)** enabled for high-performance memory search.
* **Network & REST Engine:** Retrofit paired with OkHttpClient (connection pooling, custom timeouts) and Moshi converter factory for efficient JSON serialization.
* **AI Core:** Google Gemini REST API (using standard developer API keys injected via secure Android Build Configuration).
* **Scheduling & Background Tasks:** 
  * `AlarmManager` for highly accurate, battery-friendly, low-latency local reminders.
  * `WorkManager` for deferred operations like scheduled encrypted backups and periodic maintenance of long-term memories.
* **Security & Cryptography:** Android Keystore System for hardware-backed key generation, combined with `javax.crypto` (AES/GCM/NoPadding) and PBKDF2 key-derivation for backups.

---

## 4. Subsystem Design & Architecture (Refined)

### 4.1. Bilingual Layout & Egyptian Arabic Integration Subsystem

To offer a completely natural experience for both English and Arabic, the app adopts a dual-engine linguistic foundation:

#### 1. Fluid Layout & Typography Pairing
* **Dynamic RTL Support:** The application enforces direction-aware layout structures. Compose layout containers (e.g., Row, Column) automatically reflect the current active locale's reading direction. Custom components avoid absolute horizontal modifiers (such as `paddingLeft` or `alignStart`) and instead use start/end logical parameters.
* **Typography Pairing:**
  * **English Headings & Body:** *Inter* or *Rubik* for sharp, modern, highly-readable geometric forms.
  * **Arabic Headings & Body:** *Cairo* or *Tajawal* for elegant calligraphic geometry that scales cleanly without clipping loops or diacritics.
* **Visual Direction Alignment:** Switchable visual directions for all layouts, mirroring transition animations, back buttons, and navigation elements.

#### 2. Egyptian Colloquial (Masri) and Franco-Arabic Comprehension Engine
Because Egyptian Arabic is primarily spoken and conversational (often written in Franco-Arabic or casual dialect), a multi-tier interpretation pipeline is detailed below:

```
                           [User Input Message]
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │        Language Detection Parser       │
                │     (Detects English, MSA, Masri,      │
                │        or Romanized Franco-Arabic)     │
                └────────────────────┬───────────────────┘
                                     │
                 Is Franco-Arabic?  ─┼─► [Franco Transliteration Map]
                                     │   (Translates Latin to Arabic letters)
                                     ▼
                ┌────────────────────────────────────────┐
                │       Colloquial Semantic Router       │
                │  (Unifies Masri phrases e.g. "عاوز"    │
                │   or "إيه الأخبار" to core intents)    │
                └────────────────────┬───────────────────┘
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │   Bilingual System Prompt Assembly    │
                │  (System prompt guides Gemini to parse │
                │   cultural nuances and Egyptian jokes  │
                │   or expressions natively)             │
                └────────────────────────────────────────┘
```

* **Franco-Arabic Transliterator:** A localized map utility translates standard Arabic numerals and symbol replacements (e.g., '3' for ع, '7' for ح, '5' for خ) into equivalent Arabic root stems prior to semantic classification where beneficial.
* **Colloquial System Prompt Assembly:** The core system instructions contain high-fidelity bilingual directives instructing the LLM to reply in the exact colloquial or standard language chosen by the user, matching style, humor, and regional warmth while maintaining structured response protocols.

---

### 4.2. Local-First Storage Engine (Room SQLite Schema with FTS5)

The Room database holds the local brain of AUNIO.AI. The schema is highly relational, ensuring offline capabilities. To address scalability concerns, **SQLite FTS5** is introduced for full-text indexing of memories and chat history.

#### Entity Relationship (ER) Structural Design

```
 ┌──────────────────────┐            ┌──────────────────────┐
 │  ConversationEntity  │ 1        * │    ChatMessageEntity  │
 ├──────────────────────┤◄───────────┼──────────────────────┤
 │ id: String (PK)      │            │ id: String (PK)      │
 │ title: String        │            │ convId: String (FK)  │
 │ createdAt: Long      │            │ text: String         │
 │ lastUpdated: Long    │            │ isUser: Boolean      │
 └──────────────────────┘            │ timestamp: Long      │
                                     └──────────────────────┘
                                                ▲
                                                │ (Full-Text Indexed)
                                     ┌──────────┴───────────┐
                                     │ ChatMessageFtsEntity │
                                     └──────────────────────┘

 ┌──────────────────────┐            ┌──────────────────────┐
 │     ProjectEntity    │ 1        * │      GoalEntity      │
 ├──────────────────────┤◄───────────┼──────────────────────┤
 │ id: String (PK)      │            │ id: String (PK)      │
 │ title: String        │            │ projId: String (FK)  │
 │ desc: String         │            │ title: String        │
 │ status: String       │            │ status: String       │
 │ deadline: Long       │            │ targetDate: Long     │
 └──────────────────────┘            └──────────────────────┘

 ┌──────────────────────┐            ┌──────────────────────┐
 │    ReminderEntity    │            │     MemoryEntity     │
 ├──────────────────────┤            ├──────────────────────┤
 │ id: String (PK)      │            │ id: String (PK)      │
 │ title: String        │            │ keyConcept: String   │
 │ fireTime: Long       │            │ valueDetails: String │
 │ isTriggered: Boolean │            │ score: Float         │
 │ type: String         │            │ lastAccessed: Long   │
 └──────────────────────┘            └──────────────────────┘
                                                ▲
                                                │ (Full-Text Indexed)
                                     ┌──────────┴───────────┐
                                     │   MemoryFtsEntity    │
                                     └──────────────────────┘
```

#### Database Entities & Fields Specification

1. **`ConversationEntity` / `ChatMessageEntity`:** Keep track of session history. An associated `ChatMessageFtsEntity` provides lightning-fast search over message records.
2. **`ProjectEntity`:** Represents overarching initiatives with customizable states (`Planned`, `Active`, `Completed`, `OnHold`).
3. **`GoalEntity`:** Granular milestone objects mapped to specific projects, tracking localized check-ins and progress values.
4. **`ReminderEntity`:** Represents physical, local notifications mapped with calendar date-stamps and trigger flags.
5. **`MemoryEntity`:** Long-term memory repository representing semantic units. An associated `MemoryFtsEntity` supports optimized search queries over raw text.

---

### 4.3. Long-Term Memory Subsystem (With Noise Reduction & Decay)

AUNIO.AI overcomes context-window limits and token costs through a local semantic recall and clean-up routine:

```
                  [User Inputs Message: "سافرت إسكندرية"]
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │          Semantic Key Extractor        │
                │ (Extracts "إسكندرية", "سفر", "Alexandria")│
                └────────────────────┬───────────────────┘
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │        FTS5 Optimized Keyword Search   │
                │ (Lightning-fast scan of Memory Entity) │
                └────────────────────┬───────────────────┘
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │     Context Assembly & Decay Filter    │
                │  (Fades old memories based on time,    │
                │   prioritizes fresh and high-priority) │
                └────────────────────┬───────────────────┘
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │    Memory Consolidation & Clean-up     │
                │ (Worker collapses redundant facts and  │
                │  purges low-importance semantic noise) │
                └────────────────────┬───────────────────┘
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │        Assembled Prompt Context        │
                │  (Injected into System Prompt as:      │
                │   "User notes: Enjoys trips to Alex")  │
                └────────────────────────────────────────┘
```

1. **Information Extraction:** When an AI response is generated, a background coroutine parses the dialogue exchange for long-term facts (e.g., *"My daughter's birthday is March 12"*, *"I hate seafood"*, *"بدرس هندسة في جامعة القاهرة"*).
2. **Local Memory Indexing:** These facts are compiled into structural `MemoryEntity` rows with metadata tagging (Concept, Details, Emotional Weight, Creation Timestamp).
3. **Retrieval Hook:** Upon receiving a new user prompt, the local memory FTS index is searched for matching terms (both in Arabic and English stems) on a background thread.
4. **Decay & Re-consolidation Curve:** Memories decay progressively unless accessed. Every time a memory is retrieved, its `lastAccessed` timestamp is bumped, increasing its recall priority. 
5. **Noise Reduction Engine (New):** A background consolidation worker (`WorkManager`) runs periodically to collapse redundant facts (e.g., merging "likes coffee" and "prefers espresso" into "enjoys coffee, especially espresso") and purge low-importance or obsolete semantic noise to prevent context-bloating.

---

### 4.4. Goals & Projects Engine

The task engine maintains a highly structured, relational goal progression:

* **State Transitions:** Valid states are modeled as a robust State Machine:
  ```
       ┌───────────┐      Start      ┌───────────┐      Complete      ┌───────────┐
       │  Planned  │ ──────────────► │  Active   │ ─────────────────► │ Completed │
       └─────┬─────┘                 └─────┬─────┘                    └───────────┘
             │                             │
             │ Pause                       │ Pause
             ▼                             ▼
       ┌───────────┐                 ┌───────────┐
       │  On Hold  │ ◄────────────── │ Overdue / │
       └───────────┘                 │  Delayed  │
                                     └───────────┘
  ```
* **Progress Aggregations:** Projects calculate their percentage of completion dynamically based on child goal tallies. For instance, if a project has 5 goals, and 3 are set to `Completed`, the parent project updates to `60% Completed` through local reactive Room flows.
* **Contextual Goal Interrogations:** The companion checks active overdue goals during casual chats to offer gentle motivational questions (e.g., *"How is the project 'Arabic Poetry Book' going? You planned to finish Chapter 1 yesterday"*).

---

### 4.5. Smart Reminders & Natural Language Extraction

Smart Reminders operate on two levels: automatic conversational extraction and highly reliable scheduling.

```
                  [User Input: "فكرني أكلم أحمد بكرة الساعة ٧ بالليل"]
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │         Gemini Structuring API         │
                │   (Extracts JSON: {                    │
                │      "title": "أكلم أحمد",             │
                │      "timestamp": 1782612000000        │
                │    })                                  │
                └────────────────────┬───────────────────┘
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │       Reminder Entity Creation         │
                │     (Saves to Room SQLite Database)    │
                └────────────────────┬───────────────────┘
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │        Precise Alarm Scheduling        │
                │ (Registers with Android AlarmManager)  │
                └────────────────────┬───────────────────┘
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │       Foreground Notification /        │
                │         Alarm Receiver Alert           │
                └────────────────────────────────────────┘
```

1. **Natural Language Processing:** If the user says: *"فكرني أكلم أحمد بكرة الساعة ٧ بالليل"* or *"Remind me to submit the design on Tuesday at 2 PM"*, the conversational pipeline leverages structured tool calls (using Gemini response schema formatting) to extract structured parameters: `{ "title": "أكلم أحمد", "targetTime": "2026-06-26 19:00:00" }`.
2. **Scheduling Engine:**
  * The reminder is saved in Room.
  * An Android `PendingIntent` is constructed with a unique ID matching the reminder database ID.
  * Scheduled with `AlarmManager.setExactAndAllowWhileIdle()` to ensure delivery even if the device is in Doze power-saving mode.
3. **Bilingual Broadcast Alert:** When the alarm fires, an Android BroadcastReceiver launches a localized heads-up notification with high-priority action buttons (Snooze, Complete).

---

### 4.6. Encrypted Backup Protocol (Security Toughened)

Since user companion data represents a highly personal externalized memory vault, backup security must be flawless:

1. **Symmetric Encryption Key Formulation:** The user selects a high-entropy master passphrase. A 256-bit AES key is derived using **PBKDF2WithHmacSHA256** with 100,000 iterations and a local secure random salt (or generated natively inside the hardware-backed **Android Keystore System**).
2. **Key Derivation Performance Offloading:** Key derivation is extremely computationally expensive. To prevent ANR (Application Not Responding) crashes, PBKDF2 runs strictly on a background coroutine context (`Dispatchers.Default`).
3. **AES-GCM Encryption Routine:**
   * A Room database export file or JSON dump is prepared as a standard binary byte array.
   * Encrypted using **AES-256-GCM** (Galois/Counter Mode) which provides both privacy (confidentiality) and tampering checks (authenticity) via an authentication tag.
   * A random 12-byte initialization vector (IV) is packed alongside the ciphertext.
4. **Zero-Knowledge Export Protocol:** The user receives a single, high-security backup file (`.aunio`) that can be exported locally to device storage or shared. No plain text data leaves the device sandbox.

---

### 4.7. AI-Powered Conversations & Multi-Turn Context Assembly

The conversation loop is built on a custom MVVM state cycle that aggregates context before firing an API call:

```
               [State Aggregations (Active UI Request)]
                                  │
      ┌───────────────────────────┼───────────────────────────┐
      ▼                           ▼                           ▼
[Active Goals]             [Active Reminders]          [Semantic Memories]
      │                           │                           │
      └───────────────────────────┼───────────────────────────┘
                                  ▼
                     [Context Assembly Engine]
         (Generates concise context block injected in prompt)
                                  │
                                  ▼
                     [Unified Gemini API Request]
                                  │
                                  ▼
                [Response Stream & DB Writeback]
```

1. **Context Aggregator:** Prior to calling the Gemini endpoint, the `ViewModel` queries:
   * Current system date-time (with localized Hijri / Gregorian dates).
   * Active goals & status counters.
   * Local semantic memory shards relevant to keywords in the user's latest query.
2. **Context Block Injection:** This metadata is packed into an elegant, machine-readable XML/JSON context block and appended to the developer-defined system instruction.
3. **Structured Parser:** Response content is evaluated for programmatic declarations like triggers or goal status updates (e.g., `[REMINDER: title="...", timestamp="..."]` or `[COMPLETED_GOAL: id="..."]`), performing local state mutations immediately upon generation.

---

## 5. Visual Theme & Polish Design Concept

Following the custom aesthetic instructions, AUNIO.AI will feature the **"Frosted Glass" Theme** optimized for high-end productivity layouts:

* **Background Palette:** Cosmic Obsidian Background (`#1C1B1F`) with deep dark grey gradients.
* **Foreground Accents:** Soft Lavender and Lilac glowing colors (`#D0BCFF`, `#E8DEF8`) that convey modern, advanced artificial intelligence.
* **Visual Polish Elements:**
  * **Translucent Layering:** Cards utilize subtle light-grey strokes (`#49454F`) with elevated shadows to simulate a blurred frosted-glass pane suspended over dynamic ambient gradients.
  * **Fluid Ambient Glows:** Two large blur-filtered radial gradient orbs (Top-Right in soft violet, Bottom-Left in lilac) gently rotate or pulse in the background, creating depth.
  * **Performance Fallbacks:** Large blur filters (`blur-3xl`) are optimized using hardware-accelerated Canvas drawings or vector pre-renders. The UI falls back to solid dark cards on legacy devices to maintain a fluid 60/120 FPS interface.

---

## 6. Architecture Review & Weakness Remediation

During the evaluation phase, several critical architectural bottlenecks were identified and preemptively mitigated:

| Dimension | Identified Weakness | Architectural Remediation / Improvement |
| :--- | :--- | :--- |
| **Scalability** | Standard SQLite query-matching gets slower linearly as conversation history grows. | Introduced SQLite **FTS5 (Full-Text Search)** virtual tables for both messages and memories to run keyword indexing. |
| **Performance** | PBKDF2 cryptography (100,000 iterations) blocks the Android Main UI Thread. | Explicitly bound all key-derivation procedures to a background context (`Dispatchers.Default`). |
| **Performance** | Heavy custom background blur shaders might drop frames on older, low-end Android hardware. | Configured pre-rendered vector-based gradient glows with optional solid dark styling fallbacks dynamically on legacy devices. |
| **Database Design** | Referential integrity leakage or cascading deletion orphans when projects are deleted. | Enforced strict cascading relational structures (`onDelete = ForeignKey.CASCADE`) and foreign-key constraint indexing in Room. |
| **Memory Design** | "Semantic noise" or token bloat when storing minor conversational facts, inflating context. | Integrated a background Consolidation worker running inside `WorkManager` that automatically collapses or purges obsolete memory details. |
| **Future Expansion** | Hardcoded Gemini API routes limit switching to alternate LLMs, AICore local models, or webhooks. | Created a modular **`AiEngine` Service Abstraction** to enable instant swap-outs to local models or alternative APIs. |

---

## 7. Implementation Roadmap

```
Phase 1: Setup & Groundwork
├─ Set App Package ID & Android Manifest Config
├─ Establish Dynamic LTR/RTL Locale Engine
└─ Implement Theme Color Palette & Typography

Phase 2: Persistence & Local Architecture
├─ Room Database, DAO Interfaces, FTS5 Virtual Tables
├─ Smart Alarm Broadcast Receiver Setup
└─ Local AES-256-GCM Encryption Controllers

Phase 3: Conversational Intelligence
├─ Establish Gemini REST Client with Moshi & AiEngine Abstraction
├─ Core Chat UI & Context Aggregator Pipeline
└─ Colloquial Egyptian Prompt Tuning & Franco Parser

Phase 4: Goal Tracking & Polish
├─ Goals/Projects Status Board & Dynamic Cards
├─ Smart Reminder NL Extraction Parsing
└─ Frosted Glass Visual Glow & Orbs Styling
```

---
*The design, layouts, and implementations of AUNIO.AI strictly follow this architectural blueprint, ensuring a highly polished, responsive, and secure personal AI companion experience.*
