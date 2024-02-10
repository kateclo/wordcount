package com.demo.wordcount.counter;

import com.demo.wordcount.common.CommonConstants;
import com.demo.wordcount.exception.FileReadingException;
import lombok.NonNull;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class WordCounter {
    public enum Mode {
        CONCURRENT, CHUNKED, MEM_MAPPED, CONCURRENT_MEM_MAPPED
    }

    public static final String REG_EX_WORD_BOUNDARY = "\\b+";
    public static final String REG_EX_NON_ALPHABET_AND_NON_NUMBER = "[^a-zA-Z0-9]";
    public static final int DEFAULT_COUNT_VALUE = 1;

    public abstract Map<String, Integer> retrieveTopFrequentWords(Path sourceFilepath, int frequency) throws FileReadingException;

    protected Map<String, Integer> retrieveTopFrequentWordsAndSortByDescendingOrder(@NonNull Map<String, Integer> wordCountMap, int frequency) {
        return wordCountMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(frequency)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    protected void countUniqueAndValidWords(@NonNull Map<String, Integer> wordCountMap, @NonNull String content) {
        String[] words = content.split(REG_EX_WORD_BOUNDARY);

        for (String word : words) {
            if (!word.isEmpty() && Character.isLetterOrDigit(word.charAt(0))) {

                String cleanedWord = word.replaceAll(REG_EX_NON_ALPHABET_AND_NON_NUMBER, CommonConstants.EMPTY).toLowerCase(Locale.getDefault());

                if (!cleanedWord.isEmpty()) {
                    wordCountMap.merge(cleanedWord, DEFAULT_COUNT_VALUE, Integer::sum);
                }
            }
        }
    }

}
