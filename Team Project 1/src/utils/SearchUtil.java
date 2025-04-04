package utils;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This utility class provides search functionalities using fuzzy matching and full-text indexing.
 * It supports keyword-based searching using both FuzzyWuzzy and Apache Lucene libraries.
 *
 * @author Dhruv
 */
public class SearchUtil {

    /**
     * Performs a simple fuzzy search using the FuzzyWuzzy library.
     * <p>
     * Returns items whose extracted text matches the keyword with a score of at least 60.
     *
     * @param items         The list of items to search through.
     * @param keyword       The search keyword to compare against item text.
     * @param textExtractor A function to extract searchable text from each item.
     * @param <T>           The type of items being searched.
     * @return A list of items matching the keyword above the specified threshold.
     */
    public static <T> List<T> fuzzySearch(List<T> items, String keyword, Function<T, String> textExtractor) {
        final int threshold = 60;
        List<T> results = new ArrayList<>();
        for (T item : items) {
            String text = textExtractor.apply(item);
            if (text == null) text = "";
            int score = FuzzySearch.ratio(text.toLowerCase(), keyword.toLowerCase());
            System.out.println("Text: " + text + " Score: " + score);
            if (score >= threshold) {
                results.add(item);
            }
        }
        return results;
    }

    /**
     * Performs a full-text search using Apache Lucene.
     * <p>
     * Indexes the provided items in memory and searches for matches against the keyword.
     * Returns matching items sorted by relevance.
     *
     * @param items         The list of items to index and search through.
     * @param keyword       The search keyword to query.
     * @param textExtractor A function to extract searchable text from each item.
     * @param <T>           The type of items being searched.
     * @return A list of items matching the keyword, sorted by relevance.
     * @throws Exception If an error occurs during indexing or searching.
     * @see org.apache.lucene.search.IndexSearcher
     * @see org.apache.lucene.queryparser.classic.QueryParser
     */
    public static <T> List<T> fullTextSearch(List<T> items, String keyword, Function<T, String> textExtractor) throws Exception {
        Directory directory = new ByteBuffersDirectory();
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // Index all items.
        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            String text = textExtractor.apply(item);
            Document doc = new Document();
            doc.add(new StringField("id", String.valueOf(i), Field.Store.YES));
            doc.add(new TextField("content", text, Field.Store.NO));
            indexWriter.addDocument(doc);
        }
        indexWriter.close();

        // Open index for searching.
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Convert query terms into fuzzy queries.
        String fuzzyQueryString = toFuzzyQuery(keyword);
        QueryParser parser = new QueryParser("content", analyzer);
        Query query = parser.parse(fuzzyQueryString);

        ScoreDoc[] hits = searcher.search(query, items.size()).scoreDocs;
        List<T> results = new ArrayList<>();
        for (ScoreDoc hit : hits) {
            Document doc = searcher.storedFields().document(hit.doc);
            int index = Integer.parseInt(doc.get("id"));
            results.add(items.get(index));
        }

        reader.close();
        directory.close();
        return results;
    }

    /**
     * Converts a query string into a fuzzy query format by appending a tilde ("~")
     * after each term for use with Lucene's fuzzy matching.
     *
     * @param keyword The keyword to be converted into a fuzzy query string.
     * @return A fuzzy query string where each term is suffixed with "~".
     */
    private static String toFuzzyQuery(String keyword) {
        String[] terms = keyword.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String term : terms) {
            sb.append(term).append("~ ");
        }
        return sb.toString().trim();
    }
}