#!/data/data/com.termux/files/usr/bin/bash

set -u

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT" || exit 1

. "$ROOT/tools/evolution/common.sh"

usage() {
    cat <<'USAGE'
Usage:
  ./tools/core-evolve.sh check
  ./tools/core-evolve.sh verify <step.conf>
  ./tools/core-evolve.sh run <step.conf>

Modes:
  BUILD   build a new capability
  STITCH  connect mature capabilities
  HARDEN  strengthen contracts/invariants
  MOVE    finish a vertical and move forward
USAGE
}

load_step() {
    local file="${1:-}"

    [ -n "$file" ] ||
        evolve_fail "missing step configuration"

    [ -f "$file" ] ||
        evolve_fail "step configuration not found: $file"

    VERSION=""
    TITLE=""
    MODE=""
    TARGETS=""
    READ_ONLY_PATHS=""
    FORBIDDEN_DIFF=""
    FORBIDDEN_REFERENCES=""

    # shellcheck disable=SC1090
    . "$file"

    [ -n "${VERSION:-}" ] ||
        evolve_fail "VERSION missing"

    [ -n "${TITLE:-}" ] ||
        evolve_fail "TITLE missing"

    [ -n "${MODE:-}" ] ||
        evolve_fail "MODE missing"

    case "$MODE" in
        BUILD|STITCH|HARDEN|MOVE)
            ;;
        *)
            evolve_fail "invalid MODE: $MODE"
            ;;
    esac

    TAG="core-foundation-$VERSION"
}

preflight() {
    git diff --check ||
        evolve_fail "git diff --check failed"

    git rev-parse --verify HEAD >/dev/null 2>&1 ||
        evolve_fail "HEAD unavailable"

    if git rev-parse "$TAG" >/dev/null 2>&1; then
        evolve_fail "tag already exists: $TAG"
    fi
}

guard_read_only() {
    [ -n "${READ_ONLY_PATHS:-}" ] || return 0

    local path

    for path in $READ_ONLY_PATHS; do
        [ -d "$path" ] || continue

        if grep -R -n -E \
          '\.record[[:space:]]*\(|\.clear[[:space:]]*\(|\.dispatch[[:space:]]*\(|\.execute[[:space:]]*\(' \
          "$path" \
          --include='*.kt' \
          >/dev/null 2>&1
        then
            evolve_fail "read-only guard failed: $path"
        fi
    done
}

guard_forbidden_diff() {
    [ -n "${FORBIDDEN_DIFF:-}" ] || return 0

    local changed
    changed="$(git diff --name-only -- core/src/main/java)"

    [ -z "$changed" ] && return 0

    if printf '%s\n' "$changed" |
        grep -E "$FORBIDDEN_DIFF" >/dev/null
    then
        echo "$changed" |
            grep -E "$FORBIDDEN_DIFF" |
            tail -8

        evolve_fail "forbidden production boundary changed"
    fi
}

guard_forbidden_references() {
    [ -n "${FORBIDDEN_REFERENCES:-}" ] || return 0
    [ -n "${READ_ONLY_PATHS:-}" ] || return 0

    local path

    for path in $READ_ONLY_PATHS; do
        [ -d "$path" ] || continue

        if grep -R -n -E \
          "$FORBIDDEN_REFERENCES" \
          "$path" \
          --include='*.kt' \
          >/dev/null 2>&1
        then
            evolve_fail "forbidden dependency in: $path"
        fi
    done
}

guards() {
    git diff --check ||
        evolve_fail "git diff --check failed"

    guard_read_only
    guard_forbidden_diff
    guard_forbidden_references
}

targeted_once() {
    local daemon_mode="$1"
    local log="$HOME/liliya-evolve-targeted.log"
    local args=()
    local t

    [ -n "${TARGETS:-}" ] ||
        evolve_fail "TARGETS missing"

    for t in $TARGETS; do
        args+=(--tests "$t")
    done

    if [ "$daemon_mode" = "fresh" ]; then
        timeout 240s ./gradlew :core:test \
          --no-daemon \
          "${args[@]}" \
          --console=plain \
          > "$log" 2>&1
    else
        timeout 180s ./gradlew :core:test \
          "${args[@]}" \
          --console=plain \
          > "$log" 2>&1
    fi

    return $?
}

targeted() {
    local log="$HOME/liliya-evolve-targeted.log"

    targeted_once normal
    local rc=$?

    if [ "$rc" -eq 0 ]; then
        echo "targeted: PASS"
        return 0
    fi

    if evolve_gradle_runtime_failure "$log"; then
        echo "gradle-recovery: RETRY"

        ./gradlew --stop >/dev/null 2>&1 || true

        targeted_once fresh
        rc=$?

        if [ "$rc" -eq 0 ]; then
            echo "targeted: PASS after recovery"
            return 0
        fi
    fi

    echo "targeted: FAIL rc=$rc"
    evolve_errors "$log"
    return "$rc"
}

full_core_once() {
    local daemon_mode="$1"
    local log="$HOME/liliya-evolve-full.log"

    if [ "$daemon_mode" = "fresh" ]; then
        timeout 360s ./gradlew :core:test \
          --no-daemon \
          --console=plain \
          > "$log" 2>&1
    else
        timeout 300s ./gradlew :core:test \
          --console=plain \
          > "$log" 2>&1
    fi
}

full_core() {
    local log="$HOME/liliya-evolve-full.log"

    full_core_once normal
    local rc=$?

    if [ "$rc" -eq 0 ]; then
        echo "full-core: PASS"
        return 0
    fi

    if evolve_gradle_runtime_failure "$log"; then
        echo "gradle-recovery: FULL RETRY"

        ./gradlew --stop >/dev/null 2>&1 || true

        full_core_once fresh
        rc=$?

        if [ "$rc" -eq 0 ]; then
            echo "full-core: PASS after recovery"
            return 0
        fi
    fi

    echo "full-core: FAIL rc=$rc"
    evolve_errors "$log"
    return "$rc"
}

checkpoint() {
    git diff --check ||
        evolve_fail "final git diff --check failed"

    if evolve_repo_clean; then
        evolve_fail "nothing to save"
    fi

    git add -A

    git commit \
      -m "Core Foundation $VERSION: $TITLE" \
      -m "Evolution mode: $MODE

Verified:
- declarative evolution guards pass
- targeted development contracts pass
- full core regression passes
- git diff --check passes"

    git tag "$TAG"

    echo
    echo "=== EVOLUTION RESULT ==="
    echo "version: $VERSION"
    echo "mode:    $MODE"
    echo "guards:  PASS"
    echo "targeted: PASS"
    echo "full-core: PASS"
    echo "tag:     $TAG"
    git --no-pager log -1 --oneline --decorate
}

verify_step() {
    preflight
    guards
    targeted
    guards
}

run_step() {
    preflight
    guards
    targeted || exit $?
    guards
    full_core || exit $?
    guards
    checkpoint
}

case "${1:-}" in
check)
    echo "=== EVOLUTION CHECK ==="
    git diff --check || exit 1
    git status --short
    git --no-pager log -1 --oneline --decorate
    ;;

verify)
    load_step "${2:-}"
    verify_step
    ;;

run)
    load_step "${2:-}"
    run_step
    ;;

*)
    usage
    exit 1
    ;;
esac
