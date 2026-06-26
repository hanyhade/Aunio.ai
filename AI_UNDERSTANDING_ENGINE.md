# AUNIO.AI — AI Understanding Engine Design

This document details the architecture and processing cycle of the **AUNIO.AI Understanding Engine**. It defines how the system comprehends incoming requests, injects contextual local-first state, performs cognitive routing, and synthesizes bilingual responses (English & Egyptian Arabic) with high contextual relevance.

---

## 1. The Processing Pipeline (The 6-Step Cognitive Loop)

Every message received from the user initiates a 6-stage cognitive processing cycle in the active workspace. This loop guarantees that the AI acts not as a stateless chatbot, but as an informed personal companion.

```
                  [User Input (English or Masri)]
                                │
                                ▼
         ┌──────────────────────────────────────────────┐
         │              1. Detect Intent                │
         │  - Classify input type (e.g., Query, Fact,  │
         │    Goal, Action, Reminder, Casual Conversation)│
         └──────────────────────┬───────────────────────┘
                                │
                                ▼
         ┌──────────────────────────────────────────────┐
         │               2. Check Memory                │
         │  - Query local FTS5 SQLite Memory Vault      │
         │  - Retrieve relevant user preferences & facts│
         └──────────────────────┬───────────────────────┘
                                │
                                ▼
         ┌──────────────────────────────────────────────┐
         │               3. Check Goals                 │
         │  - Fetch active milestones and status values │
         │  - Correlate with query context              │
         └──────────────────────┬───────────────────────┘
                                │
                                ▼
         ┌──────────────────────────────────────────────┐
         │              4. Check Projects               │
         │  - Fetch high-level strategic projects       │
         │  - Link active goals to their parent boards  │
         └──────────────────────┬───────────────────────┘
                                │
                                ▼
         ┌──────────────────────────────────────────────┐
         │         5. Decide If Search Is Needed        │
         │  - Determine if local DB data is sufficient  │
         │  - Decide if external search context is required│
         └──────────────────────┬───────────────────────┘
                                │
                                ▼
         ┌──────────────────────────────────────────────┐
         │             6. Generate Response             │
         │  - Synthesize prompt with context layers     │
         │  - Render response in matching language/style│
         └──────────────────────────────────────────────┘
```

---

## 2. In-Depth Phase Breakdown

### Phase 1: Intent Detection
* **Objective:** Determine the core action and semantic focus of the message.
* **Categories:**
  * `CONVERSATIONAL`: General chat, banter, greetings (e.g., *"صباح الخير"* / *"How are you?"*).
  * `RETRIEVAL`: Queries about historical facts or schedule (e.g., *"What is my sister's name?"* / *"ورائي إيه بكرة؟"*).
  * `COMMAND_REMINDER`: Requests to schedule alarm alerts (e.g., *"Remind me to study Python in 5 mins"* / *"فكرني أكلم مامي بالليل"*).
  * `COMMAND_PLANNER`: Requests to create projects or check off goals (e.g., *"Add a milestone to study Greek"*).
  * `INFORMATIONAL_FACT`: Disclosing personal detail to remember (e.g., *"أنا بفضل القهوة السادة"*).

### Phase 2: Memory Lookup
* **Objective:** Hydrate the context window with long-term structured facts.
* **Method:**
  * Perform rapid Full-Text Search (FTS5) using tokenized keywords from the user's message against the `memories` database.
  * Extract top matching facts (e.g., if user mentions *"coffee"*, retrieve `"Key Concept: coffee preference, Value: plain black coffee with no sugar"`).
  * Update the fact's `lastAccessed` timestamp to maintain a clean LRU (Least Recently Used) caching model.

### Phase 3: Goal Evaluation
* **Objective:** Check if the user is asking about active objectives, tracking progress, or marking tasks as done.
* **Method:**
  * Query the `goals` table for entries matching the semantic intent.
  * Retrieve active milestone details, percentage completeness, and upcoming deadlines.

### Phase 4: Project Context Extraction
* **Objective:** Anchor goals to high-level strategic structures.
* **Method:**
  * Fetch associated `projects` for the identified active goals.
  * Formulate a structural map of active project roadmaps so the response can speak intelligently about progress (e.g., *"You've completed 2 out of 5 goals on your Greek Mastery project!"*).

### Phase 5: Search & Fetch Decision Tree
* **Objective:** Determine if the engine needs to execute a web search, or if the offline SQLite database and pre-trained weights are sufficient.
* **Criteria for Search:**
  * Real-time queries (e.g., current weather, news, live sports).
  * Specific APIs or technical documentation.
  * When local database queries return null on factual requests.

### Phase 6: Response Synthesis & Language Rendering
* **Objective:** Generate a linguistically elegant response matching the user's tone, dialect, and layout direction (RTL/LTR).
* **Bilingual Rules:**
  * Maintain exact parity of response quality and emotional intelligence between English and Arabic.
  * Natively support colloquial **Egyptian Arabic (Masri)** without sounding overly formal (Fusha) or robotic.
  * Incorporate culture-specific idioms and Egyptian colloquialisms naturally where appropriate.

---

## 3. Bilingual Dual-Directional Synthesis Examples

Here is how the processing pipeline transforms inputs in real time:

### Example A: Egyptian Arabic (Masri) Context
* **User Input:** *"بقولك إيه، أنا نسيت هي أختي مريم عيد ميلادها إمتى؟"* (Tell me, I forgot, when is my sister Maryam's birthday?)
* **Pipeline Action:**
  1. **Intent Detection:** `RETRIEVAL`
  2. **Memory Lookup:** Query token `"مريم"`. Result found: `Key: sister Maryam birthday`, `Value: June 26th`.
  3. **Goal Check:** None.
  4. **Project Check:** None.
  5. **Search Decision:** No search needed. Local fact found.
  6. **Response Generation (Masri):** *"ولا يهمك يا بطل! عيد ميلاد أختك مريم بكرة يوم ٢٦ يونيو. تحبني أعملك منبه دلوقتي عشان تفكر تجيب الهدية؟ 🎂"* (No worries champ! Your sister Maryam's birthday is tomorrow, June 26th. Want me to set a reminder now to get her a gift?)

### Example B: English Technical Goal Context
* **User Input:** *"How am I doing on my python studies? Do I have anything due?"*
* **Pipeline Action:**
  1. **Intent Detection:** `RETRIEVAL`
  2. **Memory Lookup:** Query keyword `"python"`. Result: `"coffee preference: black"` (discarded), `"studies: learning Python"`.
  3. **Goal Check:** Query goals where title contains `"Python"`. Found: `Goal: Study Python decorators`, `Status: PLANNED`, `Target: 3 days`.
  4. **Project Check:** Parent project found: `"Python Mastery"`, `Aggregate Progress: 40%`.
  5. **Search Decision:** No search needed.
  6. **Response Generation (English):** *"You are making solid progress on your **Python Mastery** project (overall 40% complete)! 🐍 You have one outstanding milestone due in 3 days: 'Study Python decorators'. Let me know if you want to mark it done!"*
