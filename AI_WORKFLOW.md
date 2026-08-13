# LiliyaPro AI Development Workflow

## Project Identity

Project:
LiliyaPro

Current focus:
Core Foundation only.

This repository is currently developing the core runtime architecture.
Do not switch focus to Android UI, APK, model integration, or other layers unless explicitly requested.

---

## Session Recovery Rules

At the beginning of a new development session:

1. Read:

- PROJECT_STATE.md
- CHANGELOG_CORE.md
- DEVELOPMENT_LOG.md
- AI_WORKFLOW.md

2. Check repository state:

Commands:

git status

git log --oneline -10

3. Continue from the current architecture state.

Do not repeat already completed milestones.

---

## Documentation Rules

After completing an architectural milestone:

Update:

### PROJECT_STATE.md

Contains:

- current milestone
- current architecture state
- next planned direction

---

### CHANGELOG_CORE.md

Contains:

- Core Foundation versions
- commits
- architecture changes
- verification results

---

### DEVELOPMENT_LOG.md

Contains:

- problem/context
- architectural decision
- implementation result
- verification

---

## Commit Rules

Architecture commits use format:

Core Foundation vX.XX: description

Examples:

Core Foundation v0.39: introduce runtime composition root

Core Foundation v0.40: connect health and status through composition root

---

## Architecture Principles

Current direction:

CoreRuntime is a runtime coordinator.

Infrastructure creation should move into composition layer.

Preferred architecture:

CoreRuntime

    |

RuntimeComposition

    |

DefaultRuntimeComposition

    |

Runtime infrastructure components


Avoid adding new direct infrastructure construction inside CoreRuntime.

---

## Verification Rules

Before committing:

Run:

./gradlew :core:test

Commit only after successful verification.

---

## Development Philosophy

Prefer:

- small architectural steps
- clear commits
- documented decisions
- preserving history

Avoid:

- large undocumented refactoring
- repeating completed work
- changing architecture direction without recording the decision

