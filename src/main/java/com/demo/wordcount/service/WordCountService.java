package com.demo.wordcount.service;

import com.demo.wordcount.common.CommonConstants;
import com.demo.wordcount.common.FileDownloader;
import com.demo.wordcount.common.FileDownloaderFactory;
import com.demo.wordcount.common.FileUtil;
import com.demo.wordcount.counter.WordCounter;
import com.demo.wordcount.counter.WordCounterFactory;
import com.demo.wordcount.data.request.WordCountRequest;
import com.demo.wordcount.data.response.WordCountDetails;
import com.demo.wordcount.data.validator.WordCountRequestValidator;
import com.demo.wordcount.exception.IllegalArgumentException;
import com.demo.wordcount.exception.IllegalOperationException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${api.word.count.mode:CONCURRENT_MEM_MAPPED}")
    private String counterMode;

    public List<WordCountDetails> count(@NonNull WordCountRequest request)
            throws IllegalArgumentException, IllegalOperationException {

        WordCountRequestValidator.validate(request);

        Path destinationDir = FileUtil.createDirectory(CommonConstants.PROPERTY_USER_DIR, downloadsDir);

        FileDownloader fileDownloader = FileDownloaderFactory.getFileDownloader(request.getSource());
        Path downloadedFile = fileDownloader.download(request.getSource(), destinationDir.toString());

        String mode = (request.getMode() == null) ? counterMode : request.getMode();
        WordCounter wordCounter = WordCounterFactory.getWordCounter(mode);
        Map<String, Integer> sortedData = wordCounter.retrieveTopKWords(downloadedFile, request.getKvalue());

        return sortedData.entrySet().stream()
                .map(entry -> new WordCountDetails(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}