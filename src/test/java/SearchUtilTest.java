import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import utils.SearchUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link SearchUtil} class.
 * <p>
 * This test class ensures that the {@code fuzzySearch} and {@code fullTextSearch} methods function correctly
 * by verifying their behavior with various inputs, including keyword searches and relevance ordering.
 * </p>
 *
 * @author Dhruv
 * @see SearchUtil
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SearchUtilTest {

    /**
     * Tests the {@code fuzzySearch} method with a basic keyword search.
     * Ensures that the relevant item containing the keyword is found.
     */
    @Test
    @Order(1)
    public void testFuzzyFilter() {
        List<String> items = Arrays.asList("hello world", "test string", "another example", " also hello");
        List<String> results = SearchUtil.fuzzySearch(items, "hello", s -> s);
        assertTrue(results.contains("hello world"), "Should find 'hello world'");
    }

    /**
     * Tests the {@code fuzzySearch} method with multiple matches expected.
     * Verifies that all relevant items are returned.
     */
    @Test
    @Order(2)
    public void testMultipleMatches() {
        List<String> items = Arrays.asList("hello world", "test string", "another example", " also hello");
        List<String> results = SearchUtil.fuzzySearch(items, "hello", s -> s);
        assertEquals(2, results.size(), "Should find two matches");
    }

    /**
     * Tests the {@code fullTextSearch} method with short text documents.
     * Expects that documents mentioning "software" are returned.
     *
     * @throws Exception if an error occurs during full-text search execution.
     * @see SearchUtil#fullTextSearch(List, String, java.util.function.Function)
     */
    @Test
    @Order(2)
    public void testFullTextSearch() throws Exception {
        List<String> texts = Arrays.asList(
                "This document explains basic programming techniques and testing but does not cover software design.",
                "In this text, we talk about software engineering best practices like coding standards and agile methods.",
                "This article describes simple cooking recipes and meal ideas.",
                "Good software engineering requires clear design, writing good code, and testing thoroughly."
        );

        // Search for the keyword "software"
        List<String> results = SearchUtil.fullTextSearch(texts, "software", s -> s);

        // Expect at least two documents that mention "software"
        assertTrue(results.size() >= 2, "Expected at least two full-text search results for 'software'");
        for (String result : results) {
            assertTrue(result.toLowerCase().contains("software"),
                    "Result should contain 'software': " + result);
        }
    }

    /**
     * Tests the {@code fuzzySearch} method with long text documents.
     * Ensures that fuzzy search is not effective for large documents.
     */
    @Test
    @Order(3)
    public void testFuzzySearchLargeText() {
        List<String> texts = Arrays.asList(
                "This document explains basic programming techniques but does not mention the term clearly.",
                "In this text, we clearly talk about software engineering and coding standards.",
                "This article is about food recipes and does not discuss technical topics.",
                "Good software practices include writing clear code and testing regularly."
        );

        // Search for "software"
        List<String> results = SearchUtil.fuzzySearch(texts, "software", s -> s);

        // Expect no results because fuzzy search is better for keywords, not full text.
        assertTrue(results.isEmpty(), "Fuzzy search should not return irrelevant results");
    }

    /**
     * Tests that results are ordered by relevance when using {@code fullTextSearch}.
     * Documents that clearly mention "software" should rank higher.
     *
     * @throws Exception if an error occurs during full-text search execution.
     * @see SearchUtil#fullTextSearch(List, String, java.util.function.Function)
     */
    @Test
    @Order(4)
    public void testResultOrdering() throws Exception {
        List<String> texts = Arrays.asList(
                "Sofware is a term used in coding.", // Misspelled "sofware" | Should rank second
                "This text briefly mentions softwre in a long discussion with a spelling error.", // Misspelled "softwre" | Should rank lowest
                "There is no mention of engineering here.",
                "Software is important for building applications." // Should rank highest
        );

        List<String> fullTextResults = SearchUtil.fullTextSearch(texts, "software", s -> s);
        assertEquals(3, fullTextResults.size(), "Expected three full-text search results");

        // The highest scoring document should clearly include the term "software".
        String topResult = fullTextResults.get(0).toLowerCase();
        assertTrue(topResult.contains("software"), "Top result should clearly mention 'software'");
    }
}