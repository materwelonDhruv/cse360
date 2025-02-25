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

public class SearchUtil {

    /**
     * Simple fuzzy search.
     * Returns items whose text (via textExtractor) scores at least 60
     * when compared to the keyword (case-insensitive).
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
     * Full-text search using Lucene.
     * Indexes the items in memory and searches for the keyword.
     * Returns matching items sorted by relevance.
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

    // Converts a query string into a fuzzy query by appending "~" after each term.
    private static String toFuzzyQuery(String keyword) {
        String[] terms = keyword.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String term : terms) {
            sb.append(term).append("~ ");
        }
        return sb.toString().trim();
    }
}