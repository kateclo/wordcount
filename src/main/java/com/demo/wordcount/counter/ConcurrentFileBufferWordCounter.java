package com.demo.wordcount.counter;

import com.demo.wordcount.exception.FileReadingException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
public class ConcurrentFileBufferWordCounter extends WordCounter {
    @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
            justification = "Pointing to a null checking in parallel() and countUniqueAndValidWords(). Checks are okay.")
    @Override
    public Map<String, Integer> retrieveTopKWords(Path sourceFilepath, int k) throws FileReadingException {
        log.info("[CONCURRENT] Finding top K words in a file - START");
        log.info(String.format("[CONCURRENT] File: %s", sourceFilepath));

        ConcurrentHashMap<String, Integer> wordCountMap = new ConcurrentHashMap<>();

        try (Stream<String> lines = Files.lines(sourceFilepath)) {
            lines.parallel().forEach(line -> countUniqueAndValidWords(wordCountMap, line));
        } catch (IOException | SecurityException e) {
            throw new FileReadingException(sourceFilepath.toString());
        }


        log.info("[CONCURRENT] Finding top K words in a file - END");
        return retrieveTopKWordsAndSortByDescendingOrder(wordCountMap, k);
    }
}
