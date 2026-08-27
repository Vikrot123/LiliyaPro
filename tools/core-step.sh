#!/data/data/com.termux/files/usr/bin/bash

set -u

fail() {
    echo "ERROR: $1"
    exit 1
}

errors() {
    grep -E \
      'FAILED|FAILURE:|AssertionFailed|expected:|but was:|Compilation error|Unresolved reference|Exception|e: file:' \
      "$1" | tail -15
}

case "${1:-}" in

check)
    echo "=== CHECK ==="
    git diff --check || exit 1
    git status --short
    git --no-pager log -1 --oneline --decorate
    ;;

test)
    shift
    [ "$#" -gt 0 ] || fail "missing test pattern"

    LOG="$HOME/liliya-targeted.log"
    ARGS=()

    for t in "$@"; do
        ARGS+=(--tests "$t")
    done

    timeout 180s ./gradlew :core:test \
      "${ARGS[@]}" \
      --console=plain > "$LOG" 2>&1

    RC=$?

    if [ "$RC" -eq 0 ]; then
        echo "OK: targeted tests passed"
    else
        echo "ERROR: targeted tests failed rc=$RC"
        errors "$LOG"
        exit "$RC"
    fi

    git diff --check
    git status --short
    ;;

save)
    VERSION="${2:-}"
    TITLE="${3:-}"

    [ -n "$VERSION" ] || fail "missing version"
    [ -n "$TITLE" ] || fail "missing title"

    TAG="core-foundation-$VERSION"

    git rev-parse "$TAG" >/dev/null 2>&1 &&
        fail "tag already exists: $TAG"

    git diff --check || fail "git diff --check failed"

    LOG="$HOME/liliya-full.log"

    echo "=== FINAL FULL CORE ==="

    timeout 300s ./gradlew :core:test       --console=plain > "$LOG" 2>&1

    RC=$?

    if [ "$RC" -ne 0 ]; then
        echo "ERROR: full core failed rc=$RC"
        errors "$LOG"
        exit "$RC"
    fi

    echo "OK: full core passed"

    git diff --check || fail "git diff --check failed"

    git add -A

    git commit       -m "Core Foundation $VERSION: $TITLE"       -m "Verified:
- targeted development checks completed
- git diff --check passes
- full core regression passes"

    git tag "$TAG"

    echo
    echo "=== CHECKPOINT ==="
    git status --short
    git tag --points-at HEAD
    git --no-pager log -1 --oneline --decorate
    ;;

full)
    LOG="$HOME/liliya-full.log"

    timeout 300s ./gradlew :core:test \
      --console=plain > "$LOG" 2>&1

    RC=$?

    if [ "$RC" -eq 0 ]; then
        echo "OK: full core passed"
    else
        echo "ERROR: full core failed rc=$RC"
        errors "$LOG"
        exit "$RC"
    fi

    git diff --check
    git status --short
    ;;

*)
    echo "Usage:"
    echo "  ./tools/core-step.sh check"
    echo "  ./tools/core-step.sh test <pattern>..."
    echo "  ./tools/core-step.sh full"
    echo "  ./tools/core-step.sh save <version> <title>"
    exit 1
    ;;
esac
