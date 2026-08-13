* 6af3464 (HEAD -> main) Add core foundation architecture map
* c3c16cf Document core foundation architectural goals
* f060bd9 Document logging foundation as first core architecture layer
* dec93ad Add AI workflow instructions for future development sessions
* ea793f0 Add development log for core foundation history
* d67e5ce Add project state and core foundation changelog documentation
* 4796faa Core Foundation v0.40: connect health and status through composition root
* 6e81862 Core Foundation v0.39: introduce runtime composition root
* f6cfac2 Core Foundation v0.38: move module lifecycle creation into infrastructure
* 7efa309 Core Foundation v0.37: inject capability infrastructure provider
* 166c310 Core Foundation v0.36: move capability lifecycle creation to provider
* f2612a0 Core Foundation v0.35: capability infrastructure provider
* 21e65dc Core Foundation v0.34: capability infrastructure interface dependency
* 9eda785 Core Foundation v0.32: capability infrastructure abstraction
* c550fe9 Core Foundation v0.31: capability discovery registry integration
* 55b69ed Core Foundation v0.30: capability discovery lifecycle integration
* a052b41 Core Foundation v0.29: capability discovery integration
* f1e21d1 Core Foundation v0.28: module registry capability lifecycle integration
* 013f7a1 Remove temporary test fix files
* 2fa36c2 Core Foundation v0.27: module capability lifecycle integration
* f78b62a Core Foundation v0.26: module capability binding
* dd9e382 Core Foundation v0.25: capability lifecycle management
* 10d650e Core Foundation v0.24: runtime capability manager
* c0ab47b Core Foundation v0.23: capability authority matrix contract
* 0476ced Core Foundation v0.22: capability authority evaluator extraction
* 5ab84d3 Core Foundation v0.21: capability authority trace audit
* 927081b Core Foundation v0.20: capability registry foundation
* 1d7f35d Core Foundation v0.19: capability audit metadata contract
* d3e54c9 Core Foundation v0.18: capability based runtime policy
* 76630eb Core Foundation v0.17: runtime authority audit contract
* a9f4936 Core Foundation v0.16: authority aware audit trail
* df082ba Core Foundation v0.15: runtime authority enforcement
* d13480a Core Foundation v0.14: runtime action policy audit enforcement
* 89021a3 Core Foundation v0.13: reset runtime failure state on restart
* 7d26737 Core Foundation v0.12: runtime observer lifecycle reset
* 39237a1 Core Foundation v0.11: add runtime policy components
* 583ab35 Core Foundation v0.11: runtime action policy layer
* 8a173cb Core Foundation v0.10: runtime action audit trail
* 7339b12 Core Foundation v0.9: dispatcher failure resilience
* e1deaaa Core Foundation v0.8: runtime action dispatcher
* f251a05 Core Foundation v0.7: runtime action pipeline
* cda95ae Core Foundation v0.6: runtime command history and audit
* c3631bf Add runtime control implementation contract test
* c2fc714 Core Foundation v0.5: runtime control API
* 0cb16d1 Core Foundation v0.4: runtime health status and diagnostics
* 232df40 (tag: core-foundation-v0.19-runtime-observer) Add runtime observer layer
* 88cf5e4 (tag: core-foundation-v0.18-runtime-lifecycle-events) Expose lifecycle events through runtime monitor
* 03a2e70 (tag: core-foundation-v0.17-runtime-lifecycle-monitor) Integrate lifecycle recorder into runtime monitor
* 2d29955 (tag: core-foundation-v0.16-runtime-lifecycle-recorder) Add runtime lifecycle recorder
* 19a6ecb (tag: core-foundation-v0.15-runtime-monitor-integration) Integrate runtime monitor into core runtime
* 73ec94b (tag: core-foundation-v0.14-runtime-monitor) Add runtime monitor contract
* e94314e (tag: core-foundation-v0.13-runtime-diagnostics-facade) Add runtime diagnostics facade
* 6e5afd6 (tag: core-foundation-v0.12-runtime-diagnostics-service) Add runtime diagnostics service contract
* 9b3ed20 (tag: core-foundation-v0.11-runtime-diagnostics-contract) Add runtime diagnostics mapper contract
* 4a47800 (tag: core-foundation-v0.10-runtime-recovery-diagnostics) Expose runtime recovery in core diagnostics
* 0fff301 (tag: core-foundation-v0.9-runtime-recovery-snapshot) Expose runtime recovery snapshot counters
* c732377 (tag: core-foundation-v0.8-runtime-recovery) Integrate runtime recovery manager
* b2c36c6 (tag: core-foundation-v0.7.1-service-restart) Add runtime service restart support
* 80e3d74 (tag: core-foundation-v0.7-runtime-supervisor) Add runtime supervisor recovery layer
* 0088c51 (tag: core-foundation-v0.6-runtime-health) Add runtime service health diagnostics
* 47893b7 (tag: core-foundation-v0.5-runtime-diagnostics) Expose runtime service states in diagnostics
* 84d8708 (tag: core-foundation-v0.4-runtime-services) Add RuntimeServiceBootstrap lifecycle integration
* d6ded5c Integrate runtime services into CoreRuntime lifecycle
* ef4b9d9 Add runtime service registry lifecycle contracts
* ffe8dd8 Add runtime service orchestration foundation
* 6d2768a Add ModuleManager lifecycle contract test
* 39cb8e1 Add terminal lifecycle state protection to ModuleRegistry
* 7ffecd5 Update lifecycle isolation contract for terminal registry state
* e0e5992 Add start before initialization lifecycle contract test
* 3a3699c Reject module start before initialization
* 186475f Add repeated initialization lifecycle contract test
* b1a8257 Add ModuleRegistry lifecycle state machine protection
* 5658dc7 Reject late module registration after initialization
* 33005da Add duplicate registration state isolation contract
* fed78af Add duplicate registration event isolation contract
* 48bd498 Reject duplicate module registration
* 8371a26 Add module dependency cycle detection contract
* 37768f7 Add missing dependency resolution contract
* 1e9ae5e Add module stop failure isolation contract
* 374bab5 Add ModuleManager stop contract
* 0d31526 Add ModuleManager load and start contract
* f1e073a Add CoreRuntime module lifecycle orchestration contract
* ec5c285 Add critical module failure abort contract
* 83643ae Add module reverse shutdown ordering contract
* 4bcea5d Add module dependency failure propagation contract
* f8cc282 Add module dependency ordering contract
* d7a5647 Add module registry lifecycle isolation contract
* 0057031 Add full module lifecycle contract
* c993888 Add module lifecycle stop event contract
* 3aec8f5 Add module lifecycle event ordering contract
* 71e04c0 Add RuntimeEventBus lifecycle and ordering contracts
* eea1e7b Harden RuntimeEventBus listener failure isolation
* 7bcb297 Add runtime failure propagation contract test
* 54eede4 Bridge module failures into runtime event pipeline
* 14ec473 Clear failed module state after runtime stop
* 4bd73f8 Preserve failed module states in runtime diagnostics
* c39d39c Emit module failure event on dependency failure
* 0b4a78c Restore diagnostic compatibility layer
* c51091a Core Foundation: add context diagnostics contract test
* eda7b71 Core Foundation: connect runtime to context event bus
* d016fcb Core Foundation: add runtime context container
* d1cc2c7 Core Foundation: integrate runtime diagnostic events
* 87bf42d Core Foundation: add runtime diagnostic service contracts
* ce00954 Core Foundation: add failure diagnostics contract test
* bfddf86 Core Foundation: add running diagnostics contract test
* 1ac5c3e Core Foundation: add runtime diagnostics facade contract test
* f167521 Core Foundation: add runtime diagnostics facade
* 356a07b Core Foundation: add diagnostics facade contract test
* a171b78 Core Foundation: add diagnostics facade
* 066b82b Core Foundation: make diagnostic provider implement source contract
* 51a0abb Core Foundation: add diagnostic source contract
* 8d69ed9 Core Foundation: add diagnostic provider contract test
* 27a14e0 Core Foundation: add diagnostic provider
* 554227c Core Foundation: add runtime failure reason propagation
* a3fda18 Core Foundation: align failure snapshot reason contract
* def2459 Core Foundation: add runtime failure snapshot contract
* 5164770 Core Foundation: add runtime diagnostic snapshot contracts
* e1f5372 Core Foundation: add diagnostic snapshot contract test
* ddc1dff Core Foundation: add diagnostic snapshot contract model
* 7e3b305 Core Foundation: add module state query contracts
* 67c3e80 Core Foundation: add module state snapshot contract
* e4c6005 (tag: core-foundation-events-v0.1) Core Foundation: add critical module failure contracts
* cf9bdb1 Core Foundation: add module start failure event contracts
* 671f48a Core Foundation: add module failure event contracts
* 5b93ac9 Core Foundation: add module event bus
* 352aa34 Core Foundation: integrate module event pipeline
* 54f3255 Core Foundation: integrate runtime event pipeline
* 87f5c21 Core Foundation: add runtime event bus contracts
* ea78ae1 Core Foundation: add runtime state transition contracts
* ec92cfa Core Foundation: add explicit runtime state machine
* d462590 Core Foundation v0.3: fix runtime logger lifecycle and startup rollback contracts
* d8f991c Core Foundation v0.8: harden runtime lifecycle state
* daa5fb7 Core Foundation v0.7: harden lifecycle and failure contracts
* 81115b8 Core Foundation v0.6: integrate dependency resolver into registry
* c480f6d Core Foundation v0.5: add dependency resolver tests
* d596410 Core Foundation v0.5: add module dependency log events
* c30b4d0 Core Foundation v0.4: add module dependency validation
* 2865d4c Core Foundation v0.3: migrate all modules to descriptors
* 1718fa3 Core Foundation v0.3: add module descriptors
* 9f66d50 Core Foundation v0.2: module lifecycle and failure isolation
