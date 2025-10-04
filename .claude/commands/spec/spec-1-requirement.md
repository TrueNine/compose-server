---
argument-hint: [your locale language example <en_US> | <zh_CN> ] [ <your project name> ]
allowed-tools: Read, Write, Edit, MultiEdit, Glob, Grep, Bash, TodoWrite, Task
description: Progressively guide users through the first step of specification-driven development in a specified language, generating structured requirements documentation
---

This document is used to guide users through the first step of specification-driven development, collecting and organizing project requirements through progressive dialogue. The document provides complete process guidance and standardized format templates, helping users transform vague ideas into structured requirements documentation. The entire process is divided into two main phases: preparation work and requirements collection, ensuring the final output requirements documentation is executable and testable.

# [STEP-1]: Preparation Work

- Use `Search(pattern=".docs/spec/SPEC-1-REQUIREMENT.locale.md")` to check if `.docs/spec/SPEC-1-REQUIREMENTS.locale.md` exists
- If the file exists, use `Read` to load content and output a brief overview; otherwise use `Write` to initialize an empty file
- Identify the user's preferred language represented by the first parameter `$1`; subsequent free text output defaults to this language, unless the user specifies otherwise
- After completing the overview, invite the user to relax, use their imagination, and describe the ideal system completion form, then wait for user input to proceed to [STEP-2]

# [STEP-2]: Patiently Listen to User's Initial Requirements Description

- Recognize that the user is in a relaxed, divergent thinking brainstorming state; expressions may be jumpy or metaphorical
- Provide sequential guidance from an architect's perspective: use simple language, break down the system into modules, scenarios, or processes, and probe for key information segment by segment
- Listen carefully and record key information, politely asking questions to clarify ambiguous points when necessary
- Only respond when clarification or confirmation is needed; remain quietly listening at other times, avoiding frequent interruptions
- If the user says "stop" or gives an equivalent request, immediately end the conversation
- The user's divergent descriptions should first be refined, selecting content that aligns with [APPENDIX-1] structure and has executable value before using _Write_ to record; avoid copying sentence by sentence
- If no points worth writing can be filtered out temporarily, remain silent and continue listening until clear information is obtained
- Continuously iterate according to the user's pace until the user explicitly states "completion"
- After user confirmation, remind them to use `/translate` or other tools to translate the document into English, and record the translation link in `project/CLAUDE.md` to improve subsequent coding experience

```xml
<Examples>
  <GoodExample description="Actively encourage users to continue diverging" userInput="I want to create a system to help remote teams collaborate, but for now I just thought of making communication smoother.">
    Update(".docs/spec/SPEC-1-REQUIREMENT.locale.md", "- Make remote team communication smoother")
    Great initial thought! Recorded. You can first imagine the scenario when this system runs perfectly, for example, how would team members collaborate throughout a day? Feel free to share any fragments that come to mind, and we'll organize them into structured points together.
  </GoodExample>

  <BadExample description="Directly negate user ideas, lacking encouragement" userInput="I want to create a system to help remote teams collaborate, but for now I just thought of making communication smoother.">
    Your description is too vague, making it impossible to write requirements. Come back when you've thought it through.
  </BadExample>

  <GoodExample description="Maintain friendly guidance when facing particularly vague inspirations" userInput="I want to do something cool, maybe something to help friends play games together? I haven't figured it out yet.">
    Update(".docs/spec/SPEC-1-REQUIREMENT.locale.md", "- Help friends play games together more smoothly")
    Sounds interesting! I've noted this goal for now. No rush to define specific functions. You can start with small frustrations you encounter when playing games, like voice communication, team formation processes, or activity arrangements. Share a few real scenarios casually, and I'll help you organize which ones are worth recording.
  </GoodExample>

  <GoodExample description="Continue diverging when clues are too abstract" userInput="Hmm... I want to do something related to AI, but I haven't decided what specifically.">
    This direction has great potential! The current information is still quite abstract, so I won't write it to the document yet. You can first talk about the pain points you want to solve, who will use it, or the ideal vision you expect to see. The more specific you share, the easier it will be for me to help extract points worth recording.
  </GoodExample>

  <BadExample description="Press for details when user ideas are not yet formed" userInput="I want to do something cool, maybe something to help friends play games together? I haven't figured it out yet.">
    You must first determine specific functions, otherwise we can't continue. Come back when you've thought it through.
  </BadExample>
</Examples>
```


## Locale Usage Conventions

- `$1` is the locale passed by the slash command (such as `zh_CN`, `en_US`), representing the user's preferred language
- Default to using the language corresponding to `$1` when communicating with users; if users switch languages or specify special requirements, follow the latest instructions
- When generating specification documents, except for fixed English titles or keywords, all other free text uses the `$1` language
- Follow common expressions and punctuation of the `$1` language to make the copy read naturally without translation flavor
- When clarifying terms or demonstrating examples, you can first explain in the `$1` language, and supplement English comparison when necessary


# [APPENDIX-1]: Prescribed Format for Requirements Documentation

When outputting requirements documentation, you must strictly follow the following standard Markdown format specifications:

```md
# [PROJECT_NAME:- $2] User Requirements Documentation
```

**Format Description:**
- `[PROJECT_NAME:- $2]`: Placeholder to be replaced with the actual project identifier (such as `mediacms`, `carshoping`, etc.)
- Document title must be in English, following PascalCase naming conventions
- Document type is fixed as "User Requirements Documentation"

```xml
<Examples>
  <Example description="User documentation example 1">
    # mediacms User Requirements Documentation
  </Example>

  <Example description="User documentation example 2">
    # carshoping User Requirements Documentation
  </Example>

  <Example description="User documentation example 3">
    # idea-mcp-plugin User Requirements Documentation
  </Example>
</Examples>
```

After a blank line, add the project introduction section with the following format:

```md
## Introduction

This document records the detailed development requirements for developers in developing [project type] projects, ...
```

**Writing Guidelines:**
- Use second-level heading `## Introduction`
- Description should start with a sentence equivalent to "This document records the detailed development requirements for developers in developing" in the `$1` language
- Briefly explain the project type and main goals
- Length should be controlled within 2-5 sentences

```xml
<Examples>
  <Example description="MES system project example">
    ## Introduction

    此文档记录了开发者在开发 MES 系统的详细开发需求，旨在实现生产过程的数字化管理与监控。
  </Example>

  <Example description="E-commerce project example">
    ## Introduction

    此文档记录了开发者在开发电商前后端分离项目的详细开发需求，涵盖商品管理、订单处理和用户系统等核心功能。
  </Example>
</Examples>
```

After a blank line, define the target user groups with the following format:

```md
**Primary Persona:** [User group description]
```

**Writing Specifications:**
- Use the fixed English title `**Primary Persona:**`
- Use the `$1` language to describe user groups, listing multiple groups according to common separators of that language (such as Chinese enumeration commas, English commas)
- Descriptions should be concise, accurate, and maintain high relevance to the project field
- Avoid subjective evaluations or artistic expressions

```xml
<Examples>
  <GoodExample description="Manufacturing project">
    **Primary Persona:** Manufacturing employees, manufacturing developers
  </GoodExample>

  <GoodExample description="Education project">
    **Primary Persona:** University students, university teachers, modeling enthusiasts
  </GoodExample>

  <BadExample description="Error: using Chinese title">
    **主要客户群体:** University students, university teachers, modeling enthusiasts
  </BadExample>

  <BadExample description="Error: containing subjective evaluations">
    **Primary Persona:** Charismatic corporate executives, excellence-pursuing technical experts
  </BadExample>

  <BadExample description="Error: description too vague">
    **Primary Persona:** Various users, people with needs
  </BadExample>
</Examples>
```

After a blank line, add optional project constraint conditions with the following format:

```md
**Operational Constraints:**
1. [Specific constraint description]
2. [Specific constraint description]
3. [Specific constraint description]
```

Constraint type references (can be flexibly adjusted according to actual situations):
- Infrastructure: hardware configuration, network environment, deployment methods, etc.
- Technology stack: programming languages, framework choices, third-party services, etc.
- Team configuration: team size, skill structure, external collaboration, etc.
- Compliance requirements: industry standards, data security, privacy protection, etc.
- Operational support: availability targets, maintenance costs, scalability, etc.
- Business factors: budget limitations, time requirements, return on investment, etc.

```xml
<Examples>
  <GoodExample description="Video project constraints">
    **Operational Constraints:**
    1. Server performance is limited, requiring lightweight deployment and bandwidth control
    2. Default dependency on external MySQL 8; video resources can be deployed on local disk or TOS, depending on cost considerations
    3. Access and playback volume is low, but needs to ensure smooth access within the circle and easy backend maintenance
  </GoodExample>

  <GoodExample description="Financial project constraints">
    **Operational Constraints:**
    1. Must comply with national financial data security regulations, all transaction data must be encrypted and stored
    2. System availability requirement is 99.9%, annual downtime not exceeding 8.76 hours
    3. Development team of 3 people, including 1 frontend, 1 backend, 1 tester
    4. Budget limited to within 500,000, including one year of operational costs
  </GoodExample>

  <BadExample description="Description too vague">
    **Operational Constraints:**
    1. Server should be a bit better
    2. Needs to be completed quickly
    3. Budget is not quite enough
  </BadExample>

  <BadExample description="Using unprofessional expressions">
    **Operational Constraints:**
    1. Computer configuration can't be too bad, otherwise it won't run
    2. Better to use cloud services, more convenient
    3. Just find a few people to do it casually
  </BadExample>
</Examples>
```

After a blank line, add optional non-functional priority descriptions with the following format:

```md
**Non-Functional Priorities:**
1. [Priority description]
2. [Priority description]
3. [Priority description]
```

```xml
<Examples>
  <GoodExample description="Clear non-functional priorities">
    **Non-Functional Priorities:**
    1. Enable HTTPS by default, prioritize using cloud vendor free certificates
    2. Videos and thumbnails should go through TOS/CDN first; if using local storage, provide capacity monitoring and cleanup strategies
    3. Currently only need desktop experience, mobile can be iterated when future needs arise
    4. Provide containerized or scripted deployment for migration and quick recovery
    5. Implement lightweight logging and monitoring, plan regular backups of databases and key data
  </GoodExample>

  <BadExample description="Vague non-functional priorities">
    **Non-Functional Priorities:**
    1. System should be secure and stable
    2. Speed should be a bit faster
    3. Interface should look good
    4. Later maintenance should be convenient
    5. Deployment should be simple
  </BadExample>

  <GoodExample description="Quantifiable non-functional priorities">
    **Non-Functional Priorities:**
    1. All sensitive data must be AES-256 encrypted storage, transmission uses TLS 1.3
    2. Core transaction interface response time ≤ 500ms, 99% of requests need to complete within 200ms
    3. System availability ≥ 99.9%, monthly downtime ≤ 43.2 minutes
    4. Support latest two versions of Chrome/Firefox/Safari, IE11 minimum compatibility
    5. Code coverage ≥ 80%, key business 100% has integration testing
  </GoodExample>

  <BadExample description="Technology choices rather than priorities">
    **Non-Functional Priorities:**
    1. Use React framework for frontend development
    2. Backend uses Spring Boot framework
    3. Database uses MySQL 8.0
    4. Cache uses Redis
    5. Message queue uses RabbitMQ
  </BadExample>
</Examples>
```

After a blank line, add optional deferred scope descriptions with the following format:

```md
**Deferred Scope:**
1. [Feature description]
2. [Feature description]
3. [Feature description]
```

**Writing Guidelines:**
- Use the English title `**Deferred Scope:**`
- List features not considered in the current version but may need to be implemented in the future
- Each feature should be concise and highlight core value
- Avoid duplication with existing requirements
- Use the `$1` language for writing ordered list content

```xml
<Examples>
  <GoodExample description="Video platform future features">
    **Deferred Scope:**
    1. Talent marketplace recruitment capability, connecting creators and businesses
    2. Short drama sales and paid unlock modules, supporting content monetization
    3. Creator community features, supporting work exchange and collaboration
  </GoodExample>

  <GoodExample description="E-commerce platform future features">
    **Deferred Scope:**
    1. Social sharing features, allowing users to share products to various platforms
    2. Member points system, enhancing user loyalty
    3. Multi-language internationalization support, expanding overseas markets
  </GoodExample>

  <BadExample description="Description too vague">
    **Deferred Scope:**
    1. Some other features
    2. Things to add later
    3. Things to do when there's money
  </BadExample>

  <BadExample description="Duplication with current requirements">
    **Deferred Scope:**
    1. User login registration (already in basic features)
    2. Product display pages (already in core requirements)
    3. Order management functionality (already in must-implement)
  </BadExample>
</Examples>
```


Following this is the core requirements list, the most important part of the entire document, which must strictly follow the following specifications:

## Requirements Format Specifications

### Basic Structure
```md
## Requirements

### Requirement [number]: [requirement name]

**User Story:** As [user role], I want [desired functionality], so that [value gained].

#### Acceptance Criteria

1. WHEN [trigger condition] THEN [expected result]
2. WHEN [trigger condition] THEN [expected result]
3. WHEN [trigger condition] THEN [expected result]
```

### Writing Specification Requirements

1. **User Story**
- Must use standard format: `As [role], I want [function], so that [value]`
- Roles should be specific (e.g., "creator" rather than "user")
- Value should be clear (answer "why this function is needed")
- Use the `$1` language to write [role], [function], [value]

2. **Acceptance Criteria**
- Must use Given-When-Then format
- Each criterion must be independent and testable
- Avoid technical implementation details, focus on business behavior
- Use the `$1` language to write [trigger condition], [expected result]

3. **Requirements decomposition principles**
- Each requirement should be independent and have clear value
- Avoid being too large (consider splitting if more than 5 acceptance criteria)
- Avoid being too small (consider merging if fewer than 2 acceptance criteria)

```xml
<Examples>
  <GoodExample description="Complete user requirement">
    ### Requirement 3: User work management

    **User Story:** As creator, I want to be able to manage all my works, so that I can edit or delete content at any time.

    #### Acceptance Criteria

    1. WHEN creator logs in and enters personal center THEN system should display a list of all their works, including thumbnails, titles, publication time, and view counts
    2. WHEN creator clicks work edit button THEN system should jump to edit page, preserving original content and allowing modification of all information
    3. WHEN creator deletes work THEN system should require secondary confirmation, and after success remove from list and prompt user
    4. WHEN work is collected or commented by other users THEN creator should be able to see relevant statistics on management page
  </GoodExample>

  <BadExample description="Missing user value">
    ### Requirement 2: User login

    **User Story:** As user, I want to login to system.

    #### Acceptance Criteria

    1. Input username and password
    2. Click login button
    3. Login successful
  </BadExample>

  <GoodExample description="Technology-independent acceptance criteria">
    ### Requirement 5: Content recommendation

    **User Story:** As viewer, I want the system to recommend short drama content I'm interested in, so that I can discover more quality works.

    #### Acceptance Criteria

    1. WHEN viewer browses homepage THEN system should recommend similar types of works based on their viewing history
    2. WHEN viewer completes watching a work THEN system should recommend other works by related creators
    3. WHEN viewer continuously skips multiple recommendations THEN system should adjust recommendation algorithm to provide more precise content
  </GoodExample>

  <BadExample description="Containing technical implementation">
    ### Requirement 4: Video upload

    **User Story:** As creator, I want to upload video.

    #### Acceptance Criteria

    1. Call backend API interface /api/v1/videos
    2. Use MySQL to store video information
    3. Video files stored in OSS object storage
  </BadExample>

  <GoodExample description="Reasonable requirement decomposition">
    ### Requirement 7: Comment interaction

    **User Story:** As viewer, I want to comment on favorite works, so that I can exchange ideas with creators and other viewers.

    #### Acceptance Criteria

    1. WHEN viewer inputs comment on work detail page and submits THEN system should publish comment and display in comment section in real-time
    2. WHEN creator receives comment THEN system should notify creator through internal message
    3. WHEN comment contains sensitive words THEN system should automatically intercept and prompt user to modify
    4. WHEN viewer clicks a comment THEN system should display replies and likes for that comment
  </GoodExample>

  <BadExample description="Requirement too complex">
    ### Requirement 1: Complete user system

    **User Story:** As user, I want to use complete system functions.

    #### Acceptance Criteria

    1. User registration and login
    2. Personal information management
    3. Work publishing and management
    4. Comment interaction functions
    5. Message notification system
    6. Data statistics and analysis
    7. Permission management and control
    8. Payment functions
    9. Customer service system
  </BadExample>
</Examples>
```

### Requirement Priority Markings (Optional)
If you need to mark requirement priorities, you can use markers after the number:
- `[H]` - High priority
- `[M]` - Medium priority
- `[L]` - Low priority

```xml
<Examples>
  <Example description="Priority marking examples">
    ### Requirement 1[H]: User authentication
    ### Requirement 2[M]: Email notification
    ### Requirement 3[L]: Theme switching
  </Example>
</Examples>
```

```xml
<Example description="Complete example: Online education platform requirements document">
  # EduPlatform User Requirements Documentation

  ## Introduction

  此文档记录了开发者在开发在线教育平台的详细开发需求，旨在为教师和学生提供高效的在线教学与学习体验。

  **Primary Persona:** Online education teachers, university students, vocational training students, educational institution administrators

  **Operational Constraints:**
  1. Server budget is limited, needs to support at least 1000 concurrent users
  2. Must be compatible with mobile and desktop, minimum support iOS 12 and Android 8.0
  3. Video live streaming depends on third-party CDN services, need to control bandwidth costs
  4. Development team of 5 people, including 2 frontend, 2 backend, 1 tester

  **Non-Functional Priorities:**
  1. Video live streaming delay not exceeding 3 seconds, supports reconnection after disconnection
  2. User data must be encrypted storage, complying with personal information protection law requirements
  3. System availability reaches 99.5%, monthly downtime not exceeding 3.6 hours
  4. Page loading time controlled within 2 seconds

  **Deferred Scope:**
  1. AI intelligent recommendation learning content function
  2. Virtual reality (VR) immersive classroom experience
  3. Multi-language internationalization support function

  ## Requirements

  ### Requirement 1[H]: Course creation and management

  **User Story:** As teacher, I want to be able to create and manage online courses, so that I can flexibly arrange teaching content and progress.

  #### Acceptance Criteria

  1. WHEN teacher logs in and enters course management page THEN system should display "Create New Course" button and existing course list
  2. WHEN teacher clicks "Create New Course" and fills in course information THEN system should generate course homepage and support adding chapters
  3. WHEN teacher uploads video courseware THEN system should automatically transcode to multiple formats to adapt to different network environments
  4. WHEN teacher sets course price THEN system should support free, paid, and member-exclusive three modes
  5. WHEN course has student enrollment THEN teacher should receive notification and view student list

  ### Requirement 2[H]: Video live teaching

  **User Story:** As teacher, I want to conduct real-time video live teaching, so that I can interact and answer questions with students.

  #### Acceptance Criteria

  1. WHEN teacher enters live room THEN system should provide camera, microphone, and screen sharing options
  2. WHEN teacher starts live broadcast THEN system should automatically notify enrolled students
  3. WHEN students ask questions during live broadcast THEN teacher should be able to see real-time bullet comments and selectively reply
  4. WHEN network is unstable THEN system should automatically switch to lower resolution smooth mode
  5. WHEN live broadcast ends THEN system should generate playback video and automatically link to course page

  ### Requirement 3[M]: Learning progress tracking

  **User Story:** As student, I want to be able to view my learning progress, so that I can understand completion status and make study plans.

  #### Acceptance Criteria

  1. WHEN student enters personal center THEN system should display purchased course list and overall learning progress
  2. WHEN student enters course detail page THEN system should display completion status and learning time for each chapter
  3. WHEN student completes a chapter THEN system should automatically update progress and unlock next chapter
  4. WHEN student's learning time reaches system set value THEN system should pop up rest reminder
  5. WHEN student completes all courses THEN system should generate electronic certificate and support sharing

  ### Requirement 4[M]: Interactive discussion area

  **User Story:** As student, I want to be able to discuss and ask questions under courses, so that I can exchange learning experiences with classmates and teachers.

  #### Acceptance Criteria

  1. WHEN student enters course discussion area THEN system should display all discussion posts in chronological order
  2. WHEN student posts question THEN system should @ notify relevant teachers and other enrolled students
  3. WHEN teacher replies to question THEN system should mark as "answered" and highlight display
  4. WHEN student finds a certain answer useful THEN can like that answer
  5. WHEN discussion contains inappropriate content THEN system should automatically filter and submit for manual review

  ### Requirement 5[L]: Assignment submission and grading

  **User Story:** As student, I want to submit assignments online and receive teacher feedback, so that I can timely understand my learning effectiveness.

  #### Acceptance Criteria

  1. WHEN teacher publishes assignment THEN system should notify all enrolled students and display deadline
  2. WHEN student submits assignment THEN system should support text, images, documents, and video multiple formats
  3. WHEN student submits after timeout THEN system should automatically close submission entrance
  4. WHEN teacher grades assignment THEN system should support scoring, comments, and annotation functions
  5. WHEN all assignments are graded THEN system should generate class grade statistics
</Example>
```


### Q & A

**Q: How detailed should requirements be?**
A: Each requirement should be detailed enough for developers to understand and implement, but avoid over-design. Generally 3-5 acceptance criteria are appropriate.

**Q: How should acceptance criteria be written to ensure testability?**
A: Use specific, observable results, avoid vague words like "fast," "friendly," etc., and instead use specific metrics like "response time < 2 seconds."

**Q: How to judge if requirement decomposition is reasonable?**
A: If a requirement has more than 5 acceptance criteria, consider whether it can be split; if fewer than 2, consider whether it's too simple.