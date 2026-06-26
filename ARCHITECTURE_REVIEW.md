# AUNIO.AI — Comprehensive Architecture Review & Optimization Plan

This document details the complete architecture review of **AUNIO.AI**. It systematically analyzes the performance, security, scalability, memory engine, and database designs, identifies critical weaknesses, and proposes concrete, production-grade architectural improvements.

---

## 1. Executive Summary & Assessment Matrix

The initial architectural drafts established a strong foundation for a privacy-first, local-first personal AI assistant. However, moving from design to a robust production system reveals several critical architectural gaps. The matrix below summarizes our findings:

| Axis | Identified Weakness | Architectural Impact | Proposed Mitigation / Improvement |
| :--- | :--- | :--- | :--- |
| **Performance** | Synchronous API-driven cognitive analysis on all conversational turns; lacking IO thread isolation. | UI freeze, high API token usage, sluggish message response latency. | Lightweight local regular expression/rule-based "Intent Filter" bypasses the LLM for simple queries. IO dispatchers are strictly isolated. |
| **Security** | Unencrypted local SQLite storage of highly sensitive user data (age, relationships, preferences). | Complete user privacy exposure if the device is rooted or file system is accessed. | Database-level encryption at rest via SQLCipher integrated with Android Keystore. AES-GCM-256 for encrypted local backups. |
| **Scalability** | Uncapped memory database growth and flat semantic context hydration. | Context window bloat, model confusion ("lost-in-the-middle"), and performance degradation. | Asynchronous Memory Consolidation (summarizing old/conflicting records) and strict limit-offset semantic retrieval. |
| **Memory Engine** | Lacks conflict resolution and semantic entity deduplication rules. | Multiple contradictory facts (e.g., "Age = 21" and "Age = 22") stored in parallel. | Semantic overlap detection using key-distance mapping and a "Upsert or Replace" confirmation flow. |
| **Database Design** | Missing cascading actions, weak foreign key indexing, and unstructured relational joins. | Orphaned goals, slow query joins on projects, data corruption during deletes. | Explicit Foreign Key constraints (`onDelete = ForeignKey.CASCADE`) and strict index placement on foreign key columns. |

---

## 2. In-Depth Technical Review & Weakness Analysis

### 2.1 Performance: Eliminating Bottlenecks
*   **The Issue:** Running semantic extraction and classification over the Gemini API for every single user text (e.g., *"Hello"*, *"Yeah"*, *"Ok"*) introduces substantial network overhead, wastes API tokens, and compromises responsiveness.
*   **Refinement Plan:**
    1.  **Lightweight Local Gatekeeper:** Implement a light regex-based heuristic pre-classifier inside the repository layer. If the input matches simple conversational tokens, greetings, or short commands, it bypasses the cognitive memory processor entirely.
    2.  **Thread Concurrency Isolation:** Ensure that all database writes, schema queries, and network calls are strictly bound to `Dispatchers.IO`. Use Kotlin's asynchronous `Flow` to stream data reactively to the `ViewModel` (bound to `Dispatchers.Main.immediate` using `collectAsStateWithLifecycle`).

### 2.2 Security: Implementing Zero-Knowledge Encryption
*   **The Issue:** Highly confidential user data (e.g., bio, financial status, relationships) is saved in plain text within standard SQLite files on-device. This violates privacy-first core principles.
*   **Refinement Plan:**
    1.  **SQLCipher Integration:** Adopt SQLCipher for Room to encrypt the SQLite database file at rest with a 256-bit AES key.
    2.  **Android Keystore Management:** Generate and store the database encryption key securely inside the device's hardware-backed **Android Keystore System**.
    3.  **Encrypted Local Backups:** When generating a backup, do not export raw JSON. The backup payload must be encrypted using **AES-GCM-256** with a key derived from a user-provided passphrase via **PBKDF2** (Password-Based Key Derivation Function 2) with salt, saving the final payload with a secure verification checksum.

### 2.3 Scalability: Managing Infinite Contexts
*   **The Issue:** As the assistant is used over months or years, the volume of extracted memory concepts will grow indefinitely. Dumping all retrieved memories into the LLM context window during conversational rounds will cause token bloat and model drift.
*   **Refinement Plan:**
    1.  **Bounded Context Loading:** Limit memory hydration to the top 5 most relevant facts matching the user's current query, ranked by FTS5 score and recency.
    2.  **Background Memory Consolidation:** Implement an idle-time background job (via `WorkManager` when charging and on Wi-Fi). This task prompts a local or remote lightweight context compiler to analyze the database, merge redundant keys (e.g., "likes black coffee" and "prefers black coffee"), and archive obsolete context structures.

### 2.4 Memory Engine: Mitigating Contradictions & Redundancy
*   **The Issue:** If a user states *"I want to study Greek"* today, and *"I changed my mind, I want to learn German instead"* tomorrow, the database will retain both goals as equally active preferences, confusing the assistant.
*   **Refinement Plan:**
    1.  **Deduplication Layer:** Before inserting any new memory, query the database for existing keys in the same category that share semantic overlap (using rapid keyword overlap matches or string-distance metrics like Levenshtein Distance).
    2.  **Intelligent Key Merging:** If a conflict or high-similarity key is found, trigger a **Conflict Resolution Dialog**. The user is asked: *"I remember you mentioned [Old Fact]. Do you want me to update that with [New Fact], or save both?"*

### 2.5 Database Design: Solidifying Relational Integrity
*   **The Issue:** The current schema lacks explicit foreign key relations linking goals to projects, and lacks index structures on project association keys, making queries slow as data grows.
*   **Refinement Plan:**
    1.  **Define Cascades:** Update the `GoalEntity` definition to map to `ProjectEntity` via a formal `@ForeignKey` constraint with `onDelete = ForeignKey.CASCADE`. When a project is deleted, its child milestones are deleted clean.
    2.  **Targeted Indexing:** Explicitly index the `projectId` column in the goals table. This ensures that opening a project dashboard pulls all related milestones instantly in under 2ms.

---

## 3. Improved System Architecture Diagram

This end-to-end processing pipeline integrates all of our architectural improvements:

```
                          [User Input (Bilingual)]
                                      │
                                      ▼
                        ┌───────────────────────────┐
                        │  Lightweight Local Filter │ ──► (Bypasses LLM if casual)
                        └─────────────┬─────────────┘
                                      │ (Requires Analysis)
                                      ▼
                        ┌───────────────────────────┐
                        │   FTS5 + Recency Query    │
                        └─────────────┬─────────────┘
                                      │ Retrieves top-K context
                                      ▼
                        ┌───────────────────────────┐
                        │   Security Vault Decrypt  │ ──► (SQLCipher / Keystore)
                        └─────────────┬─────────────┘
                                      │
                                      ▼
                        ┌───────────────────────────┐
                        │    Deduplication Check    │ ──► (Trigger update if conflict)
                        └─────────────┬─────────────┘
                                      │ Clean Context Fed
                                      ▼
                        ┌───────────────────────────┐
                        │   Gemini Synthesis API    │
                        └─────────────┬─────────────┘
                                      │ Response & Drafts
                                      ▼
                        ┌───────────────────────────┐
                        │ Human-In-The-Loop Prompt  │ ──► (Wait for user click)
                        └─────────────┬─────────────┘
                                      │ Yes, Save
                                      ▼
                        ┌───────────────────────────┐
                        │    Room SQLite / Cipher   │
                        └───────────────────────────┘
```

---

## 4. Implementation Readiness Checklist

To transition from planning into development, we will execute the implementation according to this precise, modular roadmap:

*   [ ] **Phase 1: Secure Persistence & Schema Stabilization**
    *   Integrate SQLCipher support inside `AppDatabase.kt`.
    *   Configure foreign keys and missing indices on `GoalEntity` and `ReminderEntity`.
*   [ ] **Phase 2: Local Intent Gatekeeper & Deduplication Layer**
    *   Implement regex token patterns inside `MemoryProcessor.kt` to filter fluff.
    *   Add keyword distance matching in `AunioRepository.kt` to find conflicting facts.
*   [ ] **Phase 3: High-Performance ViewModel & M3 UI Layouts**
    *   Isolate heavy routines behind Kotlin Flows executing on `Dispatchers.IO`.
    *   Implement the modular UI views (`ChatScreen`, `GoalsScreen`, `ProjectsScreen`, `MemoryScreen`, `SettingsScreen`) keeping files compact and highly reusable.
