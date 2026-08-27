package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.integrity

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.RuntimeKnowledgeSupersessionHistory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.RuntimeKnowledgeSupersessionRecord

class DefaultRuntimeKnowledgeSupersessionIntegrityChecker(
    private val history:
        RuntimeKnowledgeSupersessionHistory
) : RuntimeKnowledgeSupersessionIntegrityChecker {

    override fun check():
        RuntimeKnowledgeSupersessionIntegrityReport {

        val records =
            history.records()

        val issues =
            mutableListOf<
                RuntimeKnowledgeSupersessionIntegrityIssue
            >()

        findSelfSupersession(
            records,
            issues
        )

        findDuplicateEdges(
            records,
            issues
        )

        findBranching(
            records,
            issues
        )

        findCycles(
            records,
            issues
        )

        return RuntimeKnowledgeSupersessionIntegrityReport(
            issues = issues.toList()
        )
    }

    private fun findSelfSupersession(
        records: List<RuntimeKnowledgeSupersessionRecord>,
        issues:
            MutableList<
                RuntimeKnowledgeSupersessionIntegrityIssue
            >
    ) {
        records
            .filter {
                it.previousKnowledge ==
                    it.replacementKnowledge
            }
            .forEach {
                issues +=
                    RuntimeKnowledgeSupersessionIntegrityIssue
                        .SelfSupersession(
                            it.previousKnowledge
                        )
            }
    }

    private fun findDuplicateEdges(
        records: List<RuntimeKnowledgeSupersessionRecord>,
        issues:
            MutableList<
                RuntimeKnowledgeSupersessionIntegrityIssue
            >
    ) {
        records
            .groupBy {
                it.previousKnowledge to
                    it.replacementKnowledge
            }
            .filterValues {
                it.size > 1
            }
            .forEach { (edge, _) ->
                issues +=
                    RuntimeKnowledgeSupersessionIntegrityIssue
                        .DuplicateEdge(
                            previousKnowledge =
                                edge.first,
                            replacementKnowledge =
                                edge.second
                        )
            }
    }

    private fun findBranching(
        records: List<RuntimeKnowledgeSupersessionRecord>,
        issues:
            MutableList<
                RuntimeKnowledgeSupersessionIntegrityIssue
            >
    ) {
        records
            .groupBy {
                it.previousKnowledge
            }
            .forEach { (previous, edges) ->
                val replacements =
                    edges
                        .map {
                            it.replacementKnowledge
                        }
                        .distinct()

                if (replacements.size > 1) {
                    issues +=
                        RuntimeKnowledgeSupersessionIntegrityIssue
                            .Branching(
                                previousKnowledge =
                                    previous,
                                replacements =
                                    replacements
                            )
                }
            }
    }

    private fun findCycles(
        records: List<RuntimeKnowledgeSupersessionRecord>,
        issues:
            MutableList<
                RuntimeKnowledgeSupersessionIntegrityIssue
            >
    ) {
        val edges =
            records
                .groupBy {
                    it.previousKnowledge
                }
                .mapValues { (_, recordsForKnowledge) ->
                    recordsForKnowledge
                        .map {
                            it.replacementKnowledge
                        }
                        .distinct()
                }

        val starts =
            (
                records.map {
                    it.previousKnowledge
                } +
                    records.map {
                        it.replacementKnowledge
                    }
            ).distinct()

        val reportedCycles =
            mutableSetOf<
                Set<RuntimeKnowledge>
            >()

        starts.forEach { start ->
            detectCycleFrom(
                start = start,
                edges = edges,
                reportedCycles = reportedCycles,
                issues = issues
            )
        }
    }

    private fun detectCycleFrom(
        start: RuntimeKnowledge,
        edges:
            Map<
                RuntimeKnowledge,
                List<RuntimeKnowledge>
            >,
        reportedCycles:
            MutableSet<Set<RuntimeKnowledge>>,
        issues:
            MutableList<
                RuntimeKnowledgeSupersessionIntegrityIssue
            >
    ) {
        val path =
            mutableListOf<RuntimeKnowledge>()

        val indexes =
            mutableMapOf<
                RuntimeKnowledge,
                Int
            >()

        var current =
            start

        while (true) {
            val existingIndex =
                indexes[current]

            if (existingIndex != null) {
                val cycle =
                    path
                        .subList(
                            existingIndex,
                            path.size
                        )
                        .toList()

                val identity =
                    cycle.toSet()

                if (
                    identity.isNotEmpty() &&
                    reportedCycles.add(identity)
                ) {
                    issues +=
                        RuntimeKnowledgeSupersessionIntegrityIssue
                            .Cycle(
                                chain = cycle
                            )
                }

                return
            }

            indexes[current] =
                path.size

            path += current

            val replacements =
                edges[current]
                    ?: return

            if (replacements.size != 1) {
                return
            }

            current =
                replacements.single()
        }
    }
}
