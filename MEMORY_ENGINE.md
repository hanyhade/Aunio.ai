# AUNIO.AI — Cognitive Memory Engine Design

This document details the architectural and semantic pipeline of the **AUNIO.AI Cognitive Memory Engine**. Built on a local-first, privacy-respecting schema, the Memory Engine acts as an auxiliary brain, ensuring that personal details are captured, validated, and structured securely.

---

## 1. Core Principles

1. **Zero Raw Chat Storage in Memory:** Under no circumstance are raw chat messages saved directly into the Memory database. Raw logs remain in the ephemeral Chat history (wipable at any time), while long-term facts reside in a separate, structured key-value/semantic ledger.
2. **Bilingual Fluidity:** Equal native understanding of English, Modern Standard Arabic (MSA), and colloquial **Egyptian Arabic (Masri)**.
3. **Strict Validation Pipeline:** Information must progress through an intentional four-tier cognitive processing cycle before database insertion.
4. **Human-in-the-Loop Clarification:** When memory confidence is low or ambiguous, the engine refuses to save silently, instead generating a natural-language confirmation query back to the user.

---

## 2. The Four-Stage Memory Pipeline

Every conversational turn from the user is evaluated in the background through the following pipeline:

```
    [User Message] ────────────────────────────────────────┐
          │                                                │
          ▼                                                ▼
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│ 1. Intent Filter │ ──►│  2. Entity Extr  │ ──►│3. Classification │
│ Skip small talk, │    │ Extract key fact │    │ PREFERENCE, GOAL,│
│ greetings, etc.  │    │ concepts/details │    │ RELATIONSHIP, etc│
└──────────────────┘    └──────────────────┘    └──────────────────┘
                                                           │
                                                           ▼
                                                ┌──────────────────┐
                                                │  4. Validation   │
                                                │ Calculate Score  │
                                                └──────────┬───────┘
                                                           │
                            ┌──────────────────────────────┴──────────────────────────────┐
                            ▼                                                             ▼
                Confidence >= 0.70                                             Confidence 0.40 - 0.69
               [Automatic Saving]                                              [Low Confidence Hook]
                        │                                                                 │
                        ▼                                                                 ▼
         ┌──────────────────────────────┐                                  ┌──────────────────────────────┐
         │     SQLite Memory Vault      │                                  │ Ask: "Should I remember this?│
         │ (FTS5 Searchable Structured) │                                  │  If yes -> Save to SQLite"   │
         └──────────────────────────────┘                                  └──────────────────────────────┘
```

### Stage 1: Intent Filtering (Noise Gate)
* **Goal:** Eliminate conversational fluff, general queries (e.g., *"What is the capital of France?"*), greetings (e.g., *"صباح الخير"*), and small talk.
* **Logic:** If the statement doesn't contain permanent, actionable details about the user's life, goals, preferences, or relationships, it is immediately discarded.

### Stage 2: Entity Extraction & Synthesis
* **Goal:** Distill unstructured prose into a clear, searchable, structured Key-Value concept pair.
* **Factual Examples:**
  * *"I am 21 years old"* ──► `Key Concept: Age`, `Value: 21`
  * *"My name is Hany"* ──► `Key Concept: Name`, `Value: Hany`
  * *"أنا ساكن في القاهرة"* ──► `Key Concept: Location`, `Value: Cairo`
  * *"أختي مريم عيد ميلادها بكرة"* ──► `Key Concept: Sister Maryam Birthday`, `Value: June 26th`

### Stage 3: Classification
The concept is categorized into one of four rigid taxonomic classes:
* **`PREFERENCE`:** Personal tastes, likes, dislikes, habits (e.g., *"I prefer black coffee with no sugar"* / *"مبحبش السمك"*).
* **`GOAL`:** Aspirations, ongoing initiatives, skills to learn (e.g., *"I want to learn Greek"* / "عاوز أتعلم برمجة بالبايثون").
* **`RELATIONSHIP`:** Details regarding family, friends, colleagues, pets (e.g., *"My sister Sarah is visiting tomorrow"* / *"كلبي اسمه ريكس"*).
* **`FACT`:** Solid personal details, age, work, school, health stats (e.g., *"I am studying engineering at Cairo University"*).

### Stage 4: Validation & Confidence Scoring
A confidence score between `0.0` and `1.0` is generated:
* **High Confidence ($\ge 0.70$):** Automatically committed to the database.
* **Medium Confidence ($0.40$ to $0.69$):** Held in memory suspension. Triggers a natural-language clarification in the active chat.
* **Low Confidence ($< 0.40$):** Discarded.

---

## 3. Bilingual Handling & Dialect Translation Examples

The extraction models are tuned to handle English and colloquial Arabic (Masri) phrases equally.

| Raw User Input (English / Egyptian) | Extracted Key Concept | Extracted Value Details | Category | Confidence |
| :--- | :--- | :--- | :--- | :--- |
| "I want to learn Greek" | `learn Greek` | User has an aspiration to learn the Greek language. | `GOAL` | `0.95` |
| "أنا بحب القهوة السادة من غير سكر" | `coffee preference` | User enjoys plain black coffee with absolutely no sugar. | `PREFERENCE` | `0.98` |
| "I am 21 years old" | `age` | User is 21 years old. | `FACT` | `0.99` |
| "My sister Sarah is visiting tomorrow" | `sister Sarah` | User's sister Sarah is visiting them tomorrow. | `RELATIONSHIP` | `0.92` |
| "أختي مريم عيد ميلادها بكرة" | `sister Maryam birthday` | User's sister Maryam has her birthday tomorrow. | `RELATIONSHIP` | `0.94` |
| "بدرس هندسة في جامعة القاهرة" | `education` | User studies engineering at Cairo University. | `FACT` | `0.96` |

---

## 4. Human-In-The-Loop: Low Confidence Loop

When the confidence score falls below `0.70` but remains above `0.40` (e.g., ambiguous phrasing or multiple conflicting details), the system initiates the **Low Confidence Clarification Cycle**:

### Scenario:
* **User Says:** *"I might take a look at German lessons soon, but I'm not sure yet."*
* **Engine Result:**
  * `isMemory`: `true`
  * `keyConcept`: `learn German`
  * `valueDetails`: `User is considering learning German.`
  * `confidenceScore`: `0.55` (Below threshold for automatic storage)
* **Execution Block:**
  1. The system does *not* insert the memory into the SQLite database.
  2. The AI response replies: *"I noticed you mentioned considering German lessons! 🇩🇪 Should I add this to your active learning goals, or wait until you decide?"*
  3. If the user replies positively (*"Yes, please"* / *"آه ضيفها"*), the UI signals the VM to commit the memory with a bumped score.
  4. If the user declines (*"No"* / *"لا"*), the item is discarded.

---

## 5. Local Database Implementation (Room Schema)

Extracted memories are saved in the `memories` table:

```kotlin
@Entity(
    tableName = "memories",
    indices = [
        Index(value = ["category"]),
        Index(value = ["lastAccessed"])
    ]
)
data class MemoryEntity(
    @PrimaryKey val id: String,
    val keyConcept: String,
    val valueDetails: String,
    val category: String, // "PREFERENCE", "GOAL", "RELATIONSHIP", "FACT"
    val score: Float,
    val lastAccessed: Long,
    val createdAt: Long = System.currentTimeMillis()
)
```

To search memories during conversational context loading, AUNIO.AI runs lightning-fast Full-Text Search (FTS5) index lookups on keywords extracted from incoming queries, bringing relevant context into the active LLM context window instantly.
