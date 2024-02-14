package com.demo.wordcount.service;

import com.demo.wordcount.common.CommonConstants;
import com.demo.wordcount.common.FileDownloader;
import com.demo.wordcount.common.FileDownloaderFactory;
import com.demo.wordcount.common.FileUtil;
import com.demo.wordcount.counter.WordCounter;
import com.demo.wordcount.data.request.WordCountRequest;
import com.demo.wordcount.data.response.WordCountDetails;
import com.demo.wordcount.exception.IllegalArgumentException;
import com.demo.wordcount.exception.IllegalOperationException;
import com.demo.wordcount.exception.UnsupportedCounterModeException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WordCountService {

    @Value("${api.word.count.dir.downloads:downloads}")
    private String downloadsDir;

    @Value("${api.word.count.mode:CONCURRENT_CHUNKED}")
    private String counterMode;


    @Autowired
    List<WordCounter> wordCounters;

    @Cacheable(value = "countCache")
    public List<WordCountDetails> count(@NonNull WordCountRequest request)
            throws IllegalArgumentException, IllegalOperationException {

        request.validate();

        Path destinationDir = FileUtil.createDirectory(CommonConstants.PROPERTY_USER_DIR, downloadsDir);

        FileDownloader fileDownloader = FileDownloaderFactory.getFileDownloader(request.getSource());
        Path downloadedFile = fileDownloader.download(request.getSource(), destinationDir.toString());

        WordCounter wordCounter = getWordCounter();
        Map<String, Integer> sortedData = wordCounter.retrieveTopFrequentWords(downloadedFile, request.getFrequency());

        return sortedData.entrySet().stream()
                .map(entry -> new WordCountDetails(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private WordCounter getWordCounter() throws UnsupportedCounterModeException {
        return wordCounters.stream()
                .filter(wordCounter -> wordCounter.getMode().equalsIgnoreCase(counterMode))
                .findFirst()
                .orElseThrow(() -> new UnsupportedCounterModeException(counterMode));
    }
}