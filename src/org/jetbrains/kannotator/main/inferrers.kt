package org.jetbrains.kannotator.main

import java.io.File
import kotlinlib.*
import org.jetbrains.kannotator.declarations.*
import org.jetbrains.kannotator.annotationsInference.nullability.*
import org.jetbrains.kannotator.annotationsInference.mutability.*
import org.jetbrains.kannotator.index.FieldDependencyInfo
import org.jetbrains.kannotator.declarations.Method
import org.jetbrains.kannotator.controlFlow.ControlFlowGraph
import org.jetbrains.kannotator.index.DeclarationIndex
import org.jetbrains.kannotator.declarations.PositionsForMethod
import org.jetbrains.kannotator.annotationsInference.Annotation
import org.jetbrains.kannotator.annotationsInference.propagation.*
import java.util.ArrayList
import java.util.Arrays

object NULLABILITY_INFERRER: AnnotationInferrer<NullabilityAnnotation> {
    override val supportsFields: Boolean = true
    override val supportsMethods: Boolean = true

    override fun resolveAnnotation(classNames: Set<String>) =
            classNamesToNullabilityAnnotation(classNames)

    override fun inferFieldAnnotations(
            fieldInfo: FieldDependencyInfo,
            controlFlowGraphBuilder: (Method) -> ControlFlowGraph,
            declarationIndex: DeclarationIndex,
            annotations: Annotations<NullabilityAnnotation>
    ): Annotations<NullabilityAnnotation> {
        return buildFieldNullabilityAnnotations(fieldInfo, controlFlowGraphBuilder, declarationIndex, annotations)
    }

    override fun inferMethodAnnotations(
            graph: ControlFlowGraph,
            positions: PositionsForMethod,
            declarationIndex: DeclarationIndex,
            annotations: Annotations<NullabilityAnnotation>
    ): Annotations<NullabilityAnnotation> {
        return buildMethodNullabilityAnnotations(graph, positions, declarationIndex, annotations)
    }


    override fun subsumes(
            position: AnnotationPosition,
            parentValue: NullabilityAnnotation,
            childValue: NullabilityAnnotation): Boolean {
        return NullabiltyLattice.subsumes(position.relativePosition, parentValue, childValue)
    }
}

object MUTABILITY_INFERRER: AnnotationInferrer<MutabilityAnnotation> {
    override val supportsFields: Boolean = false
    override val supportsMethods: Boolean = true

    override fun resolveAnnotation(classNames: Set<String>) =
            classNamesToMutabilityAnnotation(classNames)

    override fun inferFieldAnnotations(
            fieldInfo: FieldDependencyInfo,
            controlFlowGraphBuilder: (Method) -> ControlFlowGraph,
            declarationIndex: DeclarationIndex,
            annotations: Annotations<MutabilityAnnotation>
    ): Annotations<MutabilityAnnotation> {
        throw UnsupportedOperationException("Fields are not supported by mutability inference")
    }

    override fun inferMethodAnnotations(
            graph: ControlFlowGraph,
            positions: PositionsForMethod,
            declarationIndex: DeclarationIndex,
            annotations: Annotations<MutabilityAnnotation>
    ): Annotations<MutabilityAnnotation> {
        return buildMutabilityAnnotations(graph, positions, declarationIndex, annotations)
    }


    override fun subsumes(position: AnnotationPosition, parentValue: MutabilityAnnotation, childValue: MutabilityAnnotation): Boolean {
        return MutabiltyLattice.subsumes(position.relativePosition, parentValue, childValue)
    }
}