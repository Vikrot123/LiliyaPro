# LiliyaPro AI Development Workflow

## Project Identity

Project:
LiliyaPro

Current focus:
Core Foundation architecture.

The current phase is building a stable runtime foundation.

Do not switch to:
- UI development;
- APK features;
- model integration;
- higher-level AI behavior;

unless explicitly requested.

---

## Session Start

Before making changes:

1. Check repository state:

git status

2. Review recent development:

git log --oneline -10

3. Inspect relevant commits if needed:

git show <commit>

Continue from the current Git state.

Do not repeat completed work.

---

## Architecture Rules

CoreRuntime is a coordinator.

CoreRuntime should not create infrastructure components directly.

Preferred direction:

CoreRuntime
    |
    v
RuntimeComposition
    |
    v
Infrastructure components

New infrastructure should be owned by composition layers.

---

## Development Rules

Before adding a subsystem:

1. Define responsibility.
2. Define ownership.
3. Define lifecycle.
4. Add observability.
5. Explain architectural reason.

Prefer:

- small changes;
- clear commits;
- preserving Git history;
- gradual architecture evolution.

Avoid:

- large undocumented refactoring;
- adding features without purpose;
- changing architecture direction without reason.

---

## Commit Rules

Architecture commits:

Core Foundation vX.XX: description

Examples:

Core Foundation v0.39: introduce runtime composition root

Core Foundation v0.41: move runtime monitor creation into composition root

---

## Verification

Before committing architectural changes:

Run:

./gradlew :core:test

Commit only after successful verification.

---

## Main Goal

Build a modular, observable and stable runtime foundation that can later support:

- memory systems;
- AI capabilities;
- personality layers;
- adaptive runtime services.

Current priority:

Improve Core Foundation architecture before adding higher-level features.
