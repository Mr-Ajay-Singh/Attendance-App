package com.invictus.attendanceapp.core.face

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FaceMatcherTest {

    private lateinit var faceMatcher: FaceMatcher

    @Before
    fun setUp() {
        faceMatcher = FaceMatcher()
    }

    @Test
    fun calculateCosineSimilarity_identicalEmbeddings_returnsNearOne() {
        val embedding = listOf(0.5f, 0.5f, 0.5f, 0.5f)
        val similarity = faceMatcher.calculateCosineSimilarity(embedding, embedding)
        assertEquals(1.0f, similarity, 0.001f)
    }

    @Test
    fun calculateCosineSimilarity_orthogonalEmbeddings_returnsZero() {
        val embedding1 = listOf(1.0f, 0.0f)
        val embedding2 = listOf(0.0f, 1.0f)
        val similarity = faceMatcher.calculateCosineSimilarity(embedding1, embedding2)
        assertEquals(0.0f, similarity, 0.001f)
    }

    @Test
    fun isMatch_highSimilarity_returnsTrue() {
        val embedding1 = listOf(0.8f, 0.6f)
        val embedding2 = listOf(0.85f, 0.55f)
        val match = faceMatcher.isMatch(embedding1, embedding2, threshold = 0.70f)
        assertTrue(match)
    }

    @Test
    fun isMatch_lowSimilarity_returnsFalse() {
        val embedding1 = listOf(1.0f, 0.0f)
        val embedding2 = listOf(0.0f, 1.0f)
        val match = faceMatcher.isMatch(embedding1, embedding2, threshold = 0.70f)
        assertFalse(match)
    }
}
