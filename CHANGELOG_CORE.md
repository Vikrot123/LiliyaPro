# LiliyaPro Core Foundation Changelog

## v0.40
Commit:
4796faa

Title:
Core Foundation v0.40: connect health and status through composition root

Changes:
- Added health/status dependencies to RuntimeComposition.
- DefaultRuntimeComposition now creates health and status infrastructure.
- CoreRuntime no longer directly creates health/status components.

Verification:
./gradlew :core:test PASS


## v0.39
Commit:
6e81862

Title:
Core Foundation v0.39: introduce runtime composition root

Changes:
- Added RuntimeComposition abstraction.
- Added DefaultRuntimeComposition.
- Moved runtime infrastructure creation into composition layer.


## v0.38
Commit:
f6cfac2

Title:
Core Foundation v0.38: move module lifecycle creation into infrastructure

Changes:
- Lifecycle creation moved away from runtime coordination layer.
