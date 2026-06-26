# AUNIO.AI — Goals and Projects System Design

This document outlines the architecture, data flows, and interactive protocols of the **AUNIO.AI Goals & Projects System**. Engineered for a local-first environment, this system enables users to structure long-term aspirations into projects and milestones with dynamic AI-assisted tracking, while ensuring absolute user autonomy over data creation.

---

## 1. Core Principles & Governance Rules

1. **Strict User Autonomy (No Auto-Creation):** The system **MUST NEVER** create, write, or inject a goal or project directly into the database without the user's explicit consent.
2. **Interactive Suggestion Engine:** When the AI Core detects a potential goal or milestone in conversation, it generates an ephemeral "Draft Proposal" card in the UI.
3. **Human-in-the-Loop Confirmation:** Every detected objective must be formally accepted, rejected, or modified by the user before committing to the persistent SQLite database.
4. **Dual-Language Parity:** All suggestions, statuses, and action prompts support English and colloquial **Egyptian Arabic (Masri)** with equal natural tone and clarity.

---

## 2. Dynamic Status Lifecycle

To provide rich planning capabilities, both projects and goals support four rigid states:

```
          ┌──────────────────────────────────────────────────┐
          │                    DRAFTING                      │
          │         (Ephemeral AI-Suggested State)           │
          └────────────────────────┬─────────────────────────┘
                                   │ User Confirms
                                   ▼
          ┌──────────────────────────────────────────────────┐
          │                     ACTIVE                       │
          │         (Current primary focus area)             │
          └──────────┬─────────────────▲─────────────────┬───┘
                     │                 │                 │
            Complete │           Resume│                 │ Pause
                     ▼                 │                 ▼
          ┌──────────┴───────┐         │        ┌────────┴───────┐
          │    COMPLETED     │         └────────┤    PAUSED      │
          │ (Success state)  │                  │ (On-hold area) │
          └──────────┬───────┘                  └────────┬───────┘
                     │                                   │
                     │             Archive               │
                     └─────────────────┬─────────────────┘
                                       ▼
          ┌──────────────────────────────────────────────────┐
          │                    ARCHIVED                      │
          │      (Legacy records kept for history)           │
          └──────────────────────────────────────────────────┘
```

### Status Definitions:
* **`ACTIVE`:** The project/goal is in progress. The user receives reminder notifications, and context queries prioritize these details.
* **`COMPLETED`:** The objective has been successfully met. It contributes to overall progress statistics and is excluded from active alert schedules.
* **`PAUSED`:** Temporarily on-hold. Reminders are suspended, but the entity remains visible in active lists.
* **`ARCHIVED`:** Kept for chronological records and retrospective context loading. Excluded from primary dashboards.

---

## 3. The Detection & Suggestion Pipeline

The AI understanding engine runs a background classification step on every conversational turn to identify potential milestones or strategic plans:

```
                        [Incoming Conversation Turn]
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │        Intent & Topic Classifier       │
                │    Does user express a plan/aspiration?│
                └────────────────────┬───────────────────┘
                                     │ Yes (Confidence >= 0.75)
                                     ▼
                ┌────────────────────────────────────────┐
                │          Draft Goal Generator          │
                │ Formulates Title, Project Association, │
                │        and Estimated Deadline          │
                └────────────────────┬───────────────────┘
                                     │
                                     ▼
                ┌────────────────────────────────────────┐
                │          UI Suggestion Trigger         │
                │ Appends rich interactive proposal card │
                │         in the chat workspace          │
                └────────────────────┬───────────────────┘
                                     │
                 ┌───────────────────┴───────────────────┐
                 ▼                                       ▼
          [User Confirms]                         [User Rejects / Ignores]
                 │                                       │
                 ▼                                       ▼
  ┌──────────────────────────────┐                ┌──────────────────────────────┐
  │ Insert as ACTIVE in SQLite   │                │   Discard suggestion card;   │
  │     (Goals/Projects Table)   │                │   No database modifications  │
  └──────────────────────────────┘                └──────────────────────────────┘
```

### Cognitive Extraction Rules:
1. **Goal Extraction:**
   * **Title Formulation:** Extract a short action verb phrase (e.g., *"Learn Greek Verb Conjugations"*, *"تجهيز كشف حساب البنك"*).
   * **Project Association:** Query SQLite for existing active projects that match semantically. If none exist, propose creating a parent project alongside the goal.
   * **Target Date Selection:** Extract implicit timeframes (e.g., *"by next week"*, *"بكرة الصبح"*, *"end of the month"*) and resolve them into accurate UNIX epoch timestamps.

---

## 4. Human-In-The-Loop Confirmation UI Mockup

Interactive cards are embedded dynamically inside the chat interface to handle suggestions non-intrusively.

### A. English Goal Proposal Card
```
┌─────────────────────────────────────────────────────────────────────────┐
│ ✨ AUNIO.AI PLANNER SUGGESTION                                           │
│ "I noticed you want to finish setting up your financial records."       │
│                                                                         │
│ Proposed Goal: Set up financial records                                 │
│ Parent Project: Financial Independence                                  │
│ Target Date:   July 2, 2026 (1 week)                                    │
│                                                                         │
│ [ ✔ Confirm & Save ]      [ ✎ Edit Details ]       [ ✕ Decline / No ]  │
└─────────────────────────────────────────────────────────────────────────┘
```

### B. Egyptian Arabic (Masri) Goal Proposal Card
```
┌─────────────────────────────────────────────────────────────────────────┐
│ ✨ مقترح من مساعد أونيو                                                  │
│ "لاحظت إنك حابب تبدأ تتعلم لغة يوناني قريب."                              │
│                                                                         │
│ الهدف المقترح:  البدء في تعلم الأساسيات اليونانية                         │
│ المشروع التابع: إتقان اللغة اليونانية                                    │
│ التاريخ المستهدف: ٢ يوليو ٢٠٢٦ (بعد أسبوع)                              │
│                                                                         │
│ [ ✔ تأكيد وحفظ ]          [ ✎ تعديل التفاصيل ]      [ ✕ إلغاء المقترح ]  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Bilingual Synthesis Examples

Below are chronological dialog scripts showing how the extraction, suggestion, and database confirmation cycle handles bilingual input with native Masri nuances:

### Example 1: Colloquial Egyptian Arabic (Masri) Goal Detection
* **User Input:** *"بقولك إيه يا أونيو، أنا حاطط في دماغي أتعلم أساسيات لغة يوناني قبل ما الصيف يخلص، بس مش عارف أبدأ إزاي."* (Tell me Aunio, I have it in my mind to learn Greek basics before summer ends, but I don't know how to start.)
* **AI Cognitive Pipeline Process:**
  1. **Intent Analysis:** Goal intent detected with `0.94` confidence.
  2. **Entity Synthesis:**
     * `Target Activity`: Learning Greek basics.
     * `Target Timeframe`: End of summer (mapped to August 31, 2026).
  3. **Parent Project Check:** Evaluates SQLite projects. No project found for "Greek / Languages".
  4. **Formulates Suggestion Payload:**
     * `Draft Project`: *"Greek Language Mastery"* (إتقان اللغة اليونانية)
     * `Draft Goal`: *"Learn Greek Basics"* (تعلم أساسيات اليونانية)
     * `Target Date`: August 31, 2026
* **AI Response Chat Bubble:**
  *"دا فكرة ممتازة جداً يا بطل! 🇬🇷 تعلم اليوناني ممتع ومختلف. أنا جهزتلك خطة أولية وعملتلك مسودة لمشروع جديد اسمه 'إتقان اللغة اليونانية' مع هدف أولي 'تعلم أساسيات اليونانية' بمستهدف نهاية الصيف (٣١ أغسطس).*
  
  *تحب أثبتهم في قائمة مشاريعك وأهدافك ونبدأ نتابعهم سوا؟"*
* **Interface Trigger:** Displays Egyptian Arabic Proposal Card.
* **User Action:** Clicks `[ ✔ تأكيد وحفظ ]`.
* **System Action:** Inserts Project and Goal as `ACTIVE` in Room SQLite database.

### Example 2: English Milestone Addition & Context Association
* **User Input:** *"Awesome, I'll need to memorize the Greek alphabet by next Sunday to get going."*
* **AI Cognitive Pipeline Process:**
  1. **Intent Analysis:** Milestone / Goal intent detected with `0.97` confidence.
  2. **Entity Synthesis:**
     * `Target Activity`: Memorize the Greek alphabet.
     * `Target Timeframe`: Next Sunday (mapped to July 5, 2026).
  3. **Parent Project Check:** Looks up active projects. Finds active project: *"Greek Language Mastery"* (ID: `proj_greek_091`).
  4. **Formulates Suggestion Payload:**
     * `Parent Project ID`: `proj_greek_091`
     * `Draft Goal`: *"Memorize the Greek alphabet"*
     * `Target Date`: July 5, 2026
* **AI Response Chat Bubble:**
  *"Perfect first step! I've drafted a milestone for your **Greek Language Mastery** project to keep you on track. Let's aim to have the Greek alphabet memorized by next Sunday (July 5th).*
  
  *Should I lock this milestone into your planner?"*
* **Interface Trigger:** Displays English Proposal Card.
* **User Action:** Clicks `[ ✔ Confirm & Save ]`.
* **System Action:** Inserts Goal with `projectId = proj_greek_091` as `ACTIVE` in Room SQLite database.

---

## 6. Schema Refinements & Integration Plan

To implement the `Active`, `Completed`, `Paused`, and `Archived` statuses on projects and goals, the SQLite entity schemas will adopt standard enum constraints in the application repository:

```kotlin
object ProjectStatus {
    const val ACTIVE = "ACTIVE"
    const val COMPLETED = "COMPLETED"
    const val PAUSED = "PAUSED"
    const val ARCHIVED = "ARCHIVED"
}

object GoalStatus {
    const val ACTIVE = "ACTIVE"
    const val COMPLETED = "COMPLETED"
    const val PAUSED = "PAUSED"
    const val ARCHIVED = "ARCHIVED"
}
```

This guarantees complete state parity between user selections in the Planner workspace and background processing by the AI companion.
