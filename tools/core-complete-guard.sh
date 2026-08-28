#!/data/data/com.termux/files/usr/bin/bash

ok=1

echo "=== LILIYAPRO CORE COMPLETE GUARD ==="

echo
echo "--- CHECKPOINT ---"
git log -1 --oneline --decorate

echo
echo "--- LEGACY ABSENCE ---"
legacy="$(
grep -R -n -E \
'RuntimeKnowledgeLifecycleServiceHolder|DefaultRuntimeKnowledgeLifecycleServiceHolder|RuntimeKnowledgeRevisionManager|DefaultRuntimeKnowledgeRevisionManager|RuntimeCapabilityManager|MutableRuntimeCapabilityRegistry|DefaultMutableRuntimeCapabilityRegistry|RuntimeBridgeController|DefaultRuntimeBridgeController|RuntimeServiceController|DefaultRuntimeServiceController' \
core/src/main/java \
--include='*.kt' 2>/dev/null || true
)"

if [ -z "$legacy" ]; then
    echo "legacy production references: NONE"
else
    echo "$legacy" | head -30
    echo "legacy production references: PRESENT"
    ok=0
fi

echo
echo "--- CORE RUNTIME PUBLIC ENTRYPOINTS ---"

required=(
  start
  stop
  getRuntimeState
  getRuntimeStatusSnapshot
  getRuntimeHealthSnapshot
  getRuntimeRecoverySnapshot
  processAutonomousIntelligenceCycle
  dispatchRuntimeAction
  executeRuntimeCommand
  getRuntimeActionAudit
  getRuntimeCommandHistory
)

for fn in "${required[@]}"; do
    if grep -q -E "fun[[:space:]]+$fn[[:space:]]*\\(" \
        core/src/main/java/pro/liliya/core/CoreRuntime.kt
    then
        echo "PASS $fn"
    else
        echo "FAIL $fn"
        ok=0
    fi
done

echo
echo "--- COMPOSITION OWNERSHIP ---"

ownership=(
  capabilityInfrastructure
  knowledgeLifecycleCompositionHolder
  runtimeRecoveryManager
  autonomousIntelligenceCyclePipeline
)

for symbol in "${ownership[@]}"; do
    if grep -q "$symbol" \
        core/src/main/java/pro/liliya/core/runtime/composition/DefaultRuntimeComposition.kt
    then
        echo "PASS $symbol"
    else
        echo "FAIL $symbol"
        ok=0
    fi
done

echo
echo "--- UNFINISHED SOURCE MARKERS ---"

markers="$(
grep -R -n -E '\b(TODO|FIXME|WORKAROUND|HACK)\b' \
core/src/main/java \
--include='*.kt' 2>/dev/null || true
)"

if [ -z "$markers" ]; then
    echo "unfinished markers: NONE"
else
    echo "$markers" | head -20
    ok=0
fi

echo
echo "--- TARGETED CORE COMPLETE CONTRACTS ---"

if [ "$ok" -eq 1 ]; then
    tmp="$(mktemp)"

    ./gradlew :core:test \
      --tests "pro.liliya.core.CoreRuntimeAutonomousIntelligenceEndToEndContractTest" \
      --tests "pro.liliya.core.RuntimeCompositionAutonomousCommittedKnowledgeFeedbackContractTest" \
      --tests "pro.liliya.core.RuntimeCompositionAutonomousCommittedKnowledgeFailureRecoveryContractTest" \
      --tests "pro.liliya.core.CoreRuntimeAutonomousIntelligenceCycleFailureRecoveryContractTest" \
      --tests "pro.liliya.core.CoreRuntimeModuleCapabilityLifecycleTest" \
      --tests "pro.liliya.core.CoreApplicationLifecycleContractTest" \
      --console=plain >"$tmp" 2>&1

    rc=$?

    grep -E \
      'BUILD SUCCESSFUL|BUILD FAILED|FAILED|tests? completed, [1-9][0-9]* failed' \
      "$tmp" \
      | tail -20

    echo "targeted exit code: $rc"
    rm -f "$tmp"

    if [ "$rc" -ne 0 ]; then
        ok=0
    fi
fi

echo
echo "--- FULL CORE SUITE ---"

if [ "$ok" -eq 1 ]; then
    tmp="$(mktemp)"

    ./gradlew :core:test --console=plain >"$tmp" 2>&1
    rc=$?

    grep -E \
      'BUILD SUCCESSFUL|BUILD FAILED|FAILED|tests? completed, [1-9][0-9]* failed' \
      "$tmp" \
      | tail -20

    echo "full-core exit code: $rc"
    rm -f "$tmp"

    if [ "$rc" -ne 0 ]; then
        ok=0
    fi
fi

echo
echo "--- RESULT ---"

if [ "$ok" -eq 1 ]; then
    echo "CORE COMPLETE: PASS"
else
    echo "CORE COMPLETE: FAIL"
fi

echo
echo "Termux session remains open."
