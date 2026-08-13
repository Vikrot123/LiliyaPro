# LiliyaPro Core Foundation Architecture Map

## Project Purpose

LiliyaPro Core Foundation is the foundation layer of an autonomous AI runtime system.

The current phase focuses on building:
- stable runtime architecture;
- observability;
- modular infrastructure;
- lifecycle management;
- future support for AI capabilities.

---

# Current Architecture Direction

CoreRuntime is a coordinator.

CoreRuntime should not own creation of infrastructure components.

Preferred architecture:

CoreRuntime
    |
    v
RuntimeComposition
    |
    v
Infrastructure Providers
    |
    v
Runtime Components

---

# Architecture Layers

## 1. Logging Foundation

Purpose:
Provide unified system observation.

Responsibilities:
- events;
- errors;
- runtime information.

Main components:

- Logger
- LiliyaLogger
- LoggerFactory
- LoggerProvider
- LoggerContext
- LogEvent
- LogFormatter
- FileLogWriter

Logging is a foundation layer, not only a debugging tool.

---

## 2. Diagnostics System

Purpose:
Provide runtime visibility.

Responsibilities:
- collect runtime state;
- expose diagnostic snapshots;
- report failures.

Main components:

- CoreDiagnostics
- CoreDiagnosticEvent
- CoreDiagnosticSnapshot
- CoreRuntimeDiagnostics

---

## 3. Module System

Purpose:
Provide modular runtime structure.

Responsibilities:
- module registration;
- lifecycle management;
- dependency handling.

Main components:

- LiliyaModule
- ModuleRegistry
- ModuleManager
- ModuleDescriptor
- ModuleDependencyResolver

---

## 4. Runtime Lifecycle

Purpose:
Control runtime state transitions.

Responsibilities:
- startup;
- shutdown;
- lifecycle recording.

Main components:

- RuntimeLifecycleRecorder
- RuntimeLifecycleEvent
- RuntimeLifecycleRecord

---

## 5. Runtime Services

Purpose:
Provide managed runtime services.

Responsibilities:
- service registration;
- health;
- recovery;
- supervision.

Main components:

- RuntimeService
- RuntimeServiceRegistry
- RuntimeSupervisor
- RuntimeRecoveryManager

---

## 6. Capability Infrastructure

Purpose:
Prepare controlled runtime abilities.

Responsibilities:
- capability discovery;
- capability lifecycle;
- authority management.

Main components:

- RuntimeCapability
- RuntimeCapabilityRegistry
- RuntimeCapabilityDiscovery
- RuntimeCapabilityInfrastructure

---

## 7. Runtime Composition

Current architecture focus.

Purpose:
Centralize infrastructure creation.

Main components:

- RuntimeComposition
- DefaultRuntimeComposition

Currently owns:

- Observer infrastructure
- Action policy
- Lifecycle recorder
- Health infrastructure
- Failure tracking
- Recovery tracking
- Status infrastructure

---

# Development Rules

Before adding a new subsystem:

1. Define responsibility.
2. Define lifecycle.
3. Add observability.
4. Decide ownership.
5. Document architectural reason.

Avoid:

- direct infrastructure creation inside CoreRuntime;
- large undocumented refactoring;
- adding features without architectural purpose.

---

# Current Milestone

Core Foundation v0.40

Completed:

- RuntimeComposition introduced.
- Health and status infrastructure moved into composition layer.

Next candidate:

Core Foundation v0.41

Possible task:

Move RuntimeMonitor creation into RuntimeComposition after reviewing dependency flow.

