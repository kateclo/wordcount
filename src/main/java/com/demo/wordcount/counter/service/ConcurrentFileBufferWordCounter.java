package com.demo.wordcount.counter.service;

import com.demo.wordcount.counter.WordCounter;
import com.demo.wordcount.exception.FileReadingException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
@Service
public class ConcurrentFileBufferWordCounter extends WordCounter {

    @Getter
    public final String mode = "CONCURRENT";


    @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
            justification = "Pointing to a null checking in parallel() and countUniqueAndValidWords(). Checks are okay.")
    @Override
    public Map<String, Integer> retrieveTopFrequentWords(@NonNull Path sourceFilepath, int frequency) throws FileReadingException {
        log.info("[CONCURRENT] Finding top frequent words in a file - START");
        log.info(String.format("[CONCURRENT] File: %s", sourceFilepath));

        ConcurrentHashMap<String, Integer> wordCountMap = new ConcurrentHashMap<>();

        try (Stream<String> lines = Files.lines(sourceFilepath)) {
            lines.parallel().forEach(line -> countUniqueAndValidWords(wordCountMap, line));
        } catch (IOException | SecurityException | NullPointerException e) {
            throw new FileReadingException(sourceFilepath.toString());
        }


        log.info("[CONCURRENT] Finding top frequent words in a file - END");
        return retrieveTopFrequentWordsAndSortByDescendingOrder(wordCountMap, frequency);
    }
}
