---
argument-hint: [your locale language example <en_US> | <zh_CN> ] [ <your project name> ]
allowed-tools: Read, Write, Edit, MultiEdit, Glob, Grep, Bash, TodoWrite, Task
description: Progressively guide users through the first step of specification-driven development in a specified language, generating structured requirements documentation
---

This document is used to guide users through the first step of specification-driven development, collecting and organizing project requirements through progressive dialogue. The document provides complete process guidance and standardized format templates, helping users transform vague ideas into structured requirements documentation. The entire process is divided into two main phases: preparation work and requirements collection, ensuring that the final output requirements documentation has executability and testability.

# [STEP-1]: Preparation Work

- Use `Search(pattern: ".jiumate_ai/.spec/SPEC-1-REQUIREMENT.locale.md")` to check if `.jiumate_ai/.spec/SPEC-1-REQUIREMENTS.locale.md` exists
- If the file exists, use `Read(path: ".jiumate_ai/.spec/SPEC-1-REQUIREMENT.locale.md")` to load content and output a brief overview; otherwise use `Write(content: "")` to initialize an empty file
- Identify the user's preferred language represented by the first parameter `$1`, subsequent free text output defaults to this language; if the user specifies otherwise, follow the latest instruction
- After completing the overview, invite the user to stay relaxed, use their imagination, and describe the ideal system completion form, then wait for user input of their first idea to enter [STEP-2]

# [STEP-2]: Patiently listen to user's initial requirement description

- Recognize that the user is in a relaxed, divergent thinking brainstorming state, expression may be jumping or metaphorical
- Guide progressively from an architect's perspective: use simple language, break the system into modules, scenarios, or processes, and inquire about key information segment by segment
- Listen carefully and record key information, use polite questioning to clarify vague points when necessary
- Only respond when clarification or confirmation is needed, maintain quiet listening at other times, avoid frequent interruptions
- If the user says "stop" or gives an equivalent request, immediately end the conversation
- The user's divergent descriptions need to be refined first, select content that aligns with [APPENDIX-1] structure and has executable value before using _Write_ to record; avoid copying sentence by sentence
- If no points worth recording are screened out temporarily, maintain silence, continue listening until clear information is obtained
- Continue iterating according to the user's pace until the user clearly indicates "completion"
- After user confirmation is complete, remind them to use `/translate` or other tools to translate the document into English, and record the translation link in `project/CLAUDE.md` to improve subsequent coding experience

```xml
<Examples>
  <GoodExample description="Actively encourage users to continue diverging" userInput="I want to make a system that helps remote teams collaborate, but for now I just think about making communication smoother.">
    <Tooling name="Update"
             params:path=".jiumate_ai/.spec/SPEC-1-REQUIREMENT.locale.md"
             description="Make remote team communication smoother" />
    Great starting point! Recorded it. You can first imagine what this system looks like when running perfectly, for example, how team members would collaborate throughout a day? Share any fragments that come to mind, and we'll supplement them into structured points together.
  </GoodExample>

  <BadExample description="Directly negate user's ideas, lacking encouragement" userInput="I want to make a system that helps remote teams collaborate, but for now I just think about making communication smoother.">
    Your description is too vague, can't write requirements like this, think clearly and come back.
  </BadExample>

  <GoodExample description="Maintain friendly guidance when facing particularly vague inspiration" userInput="I want to do something cool, maybe something that helps friends play games together? I haven't figured it out yet.">
    <Tooling name="Update"
             params:path=".jiumate_ai/.spec/SPEC-1-REQUIREMENT.locale.md"
             description="Help friends play games together more smoothly" />
    Sounds interesting! I've noted this goal first. No need to rush into specific features for now, you can start with small problems you encounter when playing games, such as voice communication, team formation processes, or activity arrangements. Share a few real scenarios casually, and I'll help you organize which ones are worth recording.
  </GoodExample>

  <GoodExample description="Continue diverging when clues are too abstract" userInput="Hmm... I want to do something AI-related, but I haven't decided what to do specifically.">
    This direction has great potential! The information is still quite abstract at the moment, I won't write it into the document for now. You can first talk about the pain points you want to solve, who will use it, or the ideal scenarios you expect to see. The more specific you share, the easier it is for me to help you extract recordable points.
  </GoodExample>

  <BadExample description="Force details when user's ideas are not yet formed" userInput="I want to do something cool, maybe something that helps friends play games together? I haven't figured it out yet.">
    You must determine specific features first, otherwise we can't continue, come back when you've thought it through.
  </BadExample>
</Examples>
```


## Locale Usage Conventions

- `$1` is the locale passed in by the slash command (such as `zh_CN`, `en_US`), also representing the user's preferred language
- When communicating with users, default to using the language corresponding to `$1`; if the user switches languages or specifies special needs, follow the latest instruction
- When generating specification documents, except for fixed English titles or keywords, all other free text uses `$1` language
- Follow the common expressions and punctuation of `$1` language, making the copy read naturally and without translation accent
- When clarifying terminology or demonstrating examples, you can first explain in `$1` language, and supplement English comparison when necessary


# [APPENDIX-1]: Established Format for Requirements Documents

When outputting requirements documents, you must strictly follow the following standard Markdown format specifications:

```md
# [PROJECT_NAME:- $2] User Requirements Documentation
```

**Format Description:**
- `[PROJECT_NAME:- $2]`: Placeholder, needs to be replaced with the actual project identifier (such as `mediacms`, `carshoping`, etc.)
- Document title must be in English, following PascalCase naming conventions
- Document type is fixed as "User Requirements Documentation"

```xml
<Examples>
  <Example>
    # mediacms User Requirements Documentation
  </Example>

  <Example>
    # carshoping User Requirements Documentation
  </Example>

  <Example>
    # idea-mcp-plugin User Requirements Documentation
  </Example>
</Examples>
```

Leave a blank line, then add the project introduction section, format as follows:

```md
## Introduction

This document records the detailed development requirements for developers in developing [project type] projects, ...
```

**Writing Guidelines:**
- Use secondary heading `## Introduction`
- Description should start with a sentence equivalent to "This document records the detailed development requirements for developers in developing" in `$1` language
- Briefly explain the project type and main goals
- Length controlled within 2-5 sentences

```xml
<Examples>
  <Example description="MES system project example">
    ## Introduction

    This document records the detailed development requirements for developers in developing MES systems, aiming to achieve digital management and monitoring of production processes.
  </Example>

  <Example description="E-commerce project example">
    ## Introduction

    This document records the detailed development requirements for developers in developing e-commerce frontend-backend separated projects, covering core functions such as product management, order processing, and user systems.
  </Example>
</Examples>
```

Leave a blank line, then define target user groups, format as follows:

```md
**Primary Persona:** [User group description]
```

**Writing Specifications:**
- Fixed use of English title `**Primary Persona:**`
- Use `$1` language to describe user groups, and list multiple groups according to the common separators of that language (such as Chinese pause mark, English comma)
- Descriptions should be concise and accurate, maintaining high relevance to the project domain
- Avoid subjective evaluations or artistic expressions

```xml
<Examples>
  <GoodExample description="Manufacturing industry project">
    **Primary Persona:** Manufacturing employees, manufacturing developers
  </GoodExample>

  <GoodExample description="Education project">
    **Primary Persona:** College students, university teachers, student organization modeling enthusiasts
  </GoodExample>

  <BadExample description="Error: using Chinese title">
    **主要客户群体:** College students, university teachers, student organization modeling enthusiasts
  </BadExample>

  <BadExample description="Error: containing subjective evaluations">
    **Primary Persona:** Charming business executives, technology experts pursuing excellence
  </BadExample>

  <BadExample description="Error: description too vague">
    **Primary Persona:** Various users, people with needs
  </BadExample>
</Examples>
```

Leave a blank line, then add optional project constraint conditions, format as follows:

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
- Operational assurance: availability goals, maintenance costs, scalability, etc.
- Business factors: budget limitations, time requirements, return on investment, etc.

```xml
<Examples>
  <GoodExample description="Video project constraints">
    **Operational Constraints:**
    1. Server performance is limited, needs lightweight deployment and bandwidth usage control
    2. Default dependency on external MySQL 8; video resources can be deployed on local disk or TOS, depending on cost considerations
    3. Access and playback volume is low, but must ensure smooth access within the circle and easy backend maintenance
  </GoodExample>

  <GoodExample description="Finance project constraints">
    **Operational Constraints:**
    1. Must comply with national financial data security specifications, all transaction data needs encrypted storage
    2. System availability requirement 99.9%, annual downtime not exceeding 8.76 hours
    3. Development team of 3 people, including 1 frontend, 1 backend, 1 tester
    4. Budget limited within 500,000, including one year of operation costs
  </GoodExample>

  <BadExample description="Description too vague">
    **Operational Constraints:**
    1. Server should be a bit better
    2. Need to complete quickly
    3. Budget not quite enough
  </BadExample>

  <BadExample description="Using unprofessional expressions">
    **Operational Constraints:**
    1. Computer configuration can't be too bad, otherwise it won't run
    2. Better to use cloud services, more convenient
    3. Find a few people to do it casually
  </BadExample>
</Examples>
```

Leave a blank line, then add optional non-functional priority description, format as follows:

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
    2. Videos and covers prioritize via TOS/CDN; if using local storage, need to provide capacity monitoring and cleaning strategies
    3. Currently only need desktop experience, mobile can be iterated when future needs arise
    4. Provide containerized or scripted deployment for migration and quick recovery
    5. Implement lightweight logging and monitoring, and plan regular backups of databases and key data
  </GoodExample>

  <BadExample description="Vague non-functional priorities">
    **Non-Functional Priorities:**
    1. System should be secure and stable
    2. Speed should be a bit faster
    3. Interface should look good
    4. Easy to maintain later
    5. Deployment should be simple
  </BadExample>

  <GoodExample description="Quantifiable non-functional priorities">
    **Non-Functional Priorities:**
    1. All sensitive data must be AES-256 encrypted, transmission uses TLS 1.3
    2. Core transaction interface response time ≤ 500ms, 99% requests need to complete within 200ms
    3. System availability ≥ 99.9%, monthly downtime ≤ 43.2 minutes
    4. Support Chrome/Firefox/Safari latest two versions, IE11 minimum compatibility
    5. Code coverage ≥ 80%, key business 100% has integration tests
  </GoodExample>

  <BadExample description="Technology selection rather than priorities">
    **Non-Functional Priorities:**
    1. Use React framework for frontend development
    2. Backend adopts Spring Boot framework
    3. Database uses MySQL 8.0
    4. Cache uses Redis
    5. Message queue uses RabbitMQ
  </BadExample>
</Examples>
```

Leave a blank line, then add optional future functional scope description, format as follows:

```md
**Deferred Scope:**
1. [Feature description]
2. [Feature description]
3. [Feature description]
```

**Writing Guidelines:**
- Use English title `**Deferred Scope:**`
- List features not considered in current version but may need to be implemented in the future
- Each feature should be concise and highlight core value
- Avoid duplication with existing requirements
- Ordered list content uses `$1` language for writing

```xml
<Examples>
  <GoodExample description="Video platform future features">
    **Deferred Scope:**
    1. Talent market recruitment capability, connecting creators with enterprises
    2. Short drama sales and paid unlock modules, supporting content monetization
    3. Creator community features, supporting work exchange and collaboration
  </GoodExample>

  <GoodExample description="E-commerce platform future features">
    **Deferred Scope:**
    1. Social sharing functionality, allowing users to share products to various platforms
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


Then comes the core requirements list, which is the most important part of the entire document and must strictly follow the following specifications:

## Requirements Format Specifications

### Basic Structure
```md
## Requirements

### Requirement [Number]: [Requirement Name]

**User Story:** As [User Role], I want [function to complete], so that [value gained].

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
- Use `$1` language to write [role], [function], [value]

2. **Acceptance Criteria**
- Must use Given-When-Then format
- Each criterion must be independent and testable
- Avoid technical implementation details, focus on business behavior
- Use `$1` language to write [trigger condition], [expected result]

3. **Requirement Decomposition Principles**
- Each requirement should be independent and have clear value
- Avoid being too large (consider splitting if more than 5 acceptance criteria)
- Avoid being too small (consider merging if fewer than 2 acceptance criteria)

```xml
<Examples>
  <GoodExample description="Complete user requirement">
    ### Requirement 3: User Work Management

    **User Story:** As creator, I want to be able to manage all my works, so that I can edit or delete content at any time.

    #### Acceptance Criteria

    1. WHEN creator logs in and enters personal center THEN system should display list of all their works, including thumbnails, titles, publication time, and view counts
    2. WHEN creator clicks work edit button THEN system should jump to edit page, preserving original content and allowing modification of all information
    3. WHEN creator deletes work THEN system should require secondary confirmation, and after success remove from list and prompt user
    4. WHEN work is collected or commented by other users THEN creator should be able to see relevant statistics data on management page
  </GoodExample>

  <BadExample description="Missing user value">
    ### Requirement 2: User Login

    **User Story:** As user, I want to log into the system.

    #### Acceptance Criteria

    1. Enter username password
    2. Click login button
    3. Login successful
  </BadExample>

  <GoodExample description="Technology-agnostic acceptance criteria">
    ### Requirement 5: Content Recommendation

    **User Story:** As audience, I want system to recommend short drama content I'm interested in, so that I can discover more quality works.

    #### Acceptance Criteria

    1. WHEN audience browses homepage THEN system should recommend similar type works based on their viewing history
    2. WHEN audience finishes watching a work THEN system should recommend other works by related creators
    3. WHEN audience continuously skips multiple recommendations THEN system should adjust recommendation algorithm to provide more precise content
  </GoodExample>

  <BadExample description="Containing technical implementation">
    ### Requirement 4: Video Upload

    **User Story:** As creator, I want to upload videos.

    #### Acceptance Criteria

    1. Call backend API interface /api/v1/videos
    2. Use MySQL to store video information
    3. Video files stored in OSS object storage
  </BadExample>

  <GoodExample description="Reasonable requirement decomposition">
    ### Requirement 7: Comment Interaction

    **User Story:** As audience, I want to comment on works I like, so that I can exchange ideas with creators and other audience.

    #### Acceptance Criteria

    1. WHEN audience enters comment on work detail page and submits THEN system should publish comment and display in real-time in comment section
    2. WHEN creator receives comment THEN system should notify creator via internal message
    3. WHEN comment contains sensitive words THEN system should automatically intercept and prompt user to modify
    4. WHEN audience clicks a comment THEN system should display replies and likes count for that comment
  </GoodExample>

  <BadExample description="Requirement too complex">
    ### Requirement 1: Complete User System

    **User Story:** As user, I want to use complete system functionality.

    #### Acceptance Criteria

    1. User registration login
    2. Personal information management
    3. Work publishing management
    4. Comment interaction functionality
    5. Message notification system
    6. Data statistical analysis
    7. Permission management control
    8. Payment functionality
    9. Customer service system
  </BadExample>
</Examples>
```

### Requirement Priority Marking (Optional)
If need to identify requirement priority, use markers after number:
- `[H]` - High priority
- `[M]` - Medium priority
- `[L]` - Low priority

```xml
<Examples>
  <Example description="Priority marking example">
    ### Requirement 1[H]: User Authentication
    ### Requirement 2[M]: Email Notification
    ### Requirement 3[L]: Theme Switching
  </Example>
</Examples>
```

```xml
<Example description="Complete example: Online education platform requirements document">
  # EduPlatform User Requirements Documentation

  ## Introduction

  This document records the detailed development requirements for developers in developing online education platforms, aiming to provide efficient online teaching and learning experiences for teachers and students.

  **Primary Persona:** Online education teachers, college students, vocational training students, educational institution administrators

  **Operational Constraints:**
  1. Server budget is limited, needs to support at least 1000 concurrent users
  2. Must be compatible with mobile and desktop, minimum support iOS 12 and Android 8.0
  3. Video live streaming depends on third-party CDN service, need to control bandwidth costs
  4. Development team of 5 people, including 2 frontend, 2 backend, 1 tester

  **Non-Functional Priorities:**
  1. Video live streaming delay not exceeding 3 seconds, supporting reconnection after disconnection
  2. User data must be encrypted, complying with personal information protection law requirements
  3. System availability reaching 99.5%, monthly downtime not exceeding 3.6 hours
  4. Page loading time controlled within 2 seconds

  **Deferred Scope:**
  1. AI intelligent recommendation learning content functionality
  2. Virtual Reality (VR) immersive classroom experience
  3. Multi-language internationalization support functionality

  ## Requirements

  ### Requirement 1[H]: Course Creation and Management

  **User Story:** As teacher, I want to be able to create and manage online courses, so that I can flexibly arrange teaching content and progress.

  #### Acceptance Criteria

  1. WHEN teacher logs in and enters course management page THEN system should display "Create New Course" button and existing course list
  2. WHEN teacher clicks "Create New Course" and fills in course information THEN system should generate course homepage and support adding chapters
  3. WHEN teacher uploads video courseware THEN system should automatically transcode to multiple formats to adapt to different network environments
  4. WHEN teacher sets course price THEN system should support free, paid, and member-exclusive three modes
  5. WHEN course has student enrollment THEN teacher should receive notification and view student list

  ### Requirement 2[H]: Video Live Teaching

  **User Story:** As teacher, I want to conduct real-time video live teaching, so that I can interact and answer questions with students.

  #### Acceptance Criteria

  1. WHEN teacher enters live room THEN system should provide camera, microphone, and screen sharing options
  2. WHEN teacher starts live broadcast THEN system should automatically notify enrolled students
  3. WHEN students ask questions during live broadcast THEN teacher should be able to see real-time danmaku and selectively reply
  4. WHEN network is unstable THEN system should automatically switch to lower clarity smooth mode
  5. WHEN live broadcast ends THEN system should generate playback video and automatically associate to course page

  ### Requirement 3[M]: Learning Progress Tracking

  **User Story:** As student, I want to be able to view my learning progress, so that I can understand completion status and make study plans.

  #### Acceptance Criteria

  1. WHEN student enters personal center THEN system should display purchased course list and overall learning progress
  2. WHEN student enters course detail page THEN system should show completion status and learning time for each chapter
  3. WHEN student completes a chapter THEN system should automatically update progress and unlock next chapter
  4. WHEN student's learning time reaches system set value THEN system should pop up rest reminder
  5. WHEN student completes all courses THEN system should generate electronic certificate and support sharing

  ### Requirement 4[M]: Interactive Discussion Area

  **User Story:** As student, I want to be able to discuss and ask questions under courses, so that I can exchange learning experiences with classmates and teachers.

  #### Acceptance Criteria

  1. WHEN student enters course discussion area THEN system should display all discussion posts in chronological order
  2. WHEN student posts question THEN system should @ notify relevant teachers and other enrolled students
  3. WHEN teacher replies to question THEN system should mark as "answered" and highlight display
  4. WHEN student finds a certain answer useful THEN can like that answer
  5. WHEN discussion contains inappropriate content THEN system should automatically filter and submit for manual review

  ### Requirement 5[L]: Assignment Submission and Grading

  **User Story:** As student, I want to submit assignments online and receive teacher feedback, so that I can understand my learning effectiveness in a timely manner.

  #### Acceptance Criteria

  1. WHEN teacher publishes assignment THEN system should notify all enrolled students and display deadline
  2. WHEN student submits assignment THEN system should support text, images, documents, and video multiple formats
  3. WHEN student submits after timeout THEN system should automatically close submission entry
  4. WHEN teacher grades assignment THEN system should support scoring, comments, and annotation functions
  5. WHEN all assignments are graded THEN system should generate class grade statistics
</Example>
```


### Q & A

**Q: How detailed should requirements be?**
A: Each requirement should be detailed enough for developers to understand and implement, but avoid over-design. Generally 3-5 acceptance criteria are appropriate.

**Q: How to write acceptance criteria to ensure testability?**
A: Use specific, observable results, avoid vague words like "fast", "friendly", etc., change to specific metrics like "response time < 2 seconds".

**Q: How to determine if requirement decomposition is reasonable?**
A: If a requirement has more than 5 acceptance criteria, consider whether it can be split; if fewer than 2, consider whether it's too simple.