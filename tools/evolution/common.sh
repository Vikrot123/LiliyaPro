#!/data/data/com.termux/files/usr/bin/bash

evolve_fail() {
    echo "ERROR: $1"
    exit 1
}

evolve_errors() {
    local log="$1"

    grep -E \
      'FAILED|FAILURE:|AssertionFailed|expected:|but was:|Compilation error|Unresolved reference|Exception|e: file:|Could not create task|Could not determine the dependencies' \
      "$log" \
      | tail -12
}

evolve_gradle_runtime_failure() {
    local log="$1"

    grep -q -E \
      'Could not create task.*checkKotlinGradlePluginConfigurationErrors|DefaultTaskCollection#configureEach|Could not determine the dependencies of task' \
      "$log"
}

evolve_repo_clean() {
    [ -z "$(git status --short)" ]
}

evolve_require_clean() {
    evolve_repo_clean ||
        evolve_fail "working tree must be clean before evolution"
}
