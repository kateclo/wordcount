package com.demo.wordcount.counter.service;

import com.demo.wordcount.common.CommonConstants;
import com.demo.wordcount.counter.ChunkingWordCounter;
import com.demo.wordcount.exception.FileReadingException;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Slf4j
@Service
public class ConcurrentMemoryMappedChunkingWordCounter extends ChunkingWordCounter {
    private static final int MAX_CONCURRENT_TASKS = 10;

    @Value("${api.word.count.chunked.buffer.bytes.size:1048576}")
    private int bufferSizeInBytes;

    @Value("${api.word.count.chunked.word.maxLength:100}")
    private int maxChunkedWordLength;

    @Getter
    private final String mode = "CONCURRENT_CHUNKED";

    @Override
    public Map<String, Integer> retrieveTopFrequentWords(@NonNull Path sourceFilepath, int frequency) throws FileReadingException {

        log.info("[CONCURRENT_CHUNKED] Finding top frequent words in a file - START");
        log.info(String.format("[CONCURRENT_CHUNKED] File: %s", sourceFilepath));

        ConcurrentHashMap<String, Integer> wordCounts = new ConcurrentHashMap<>();

        try (FileChannel fileChannel = FileChannel.open(sourceFilepath, StandardOpenOption.READ)) {
            long fileSize = fileChannel.size();

            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);

            processMappedBuffer(buffer, wordCounts, fileSize);

        } catch (IOException | SecurityException | IndexOutOfBoundsException | FileReadingException | NullPointerException e) {
            throw new FileReadingException(sourceFilepath.toString());
        }


        log.info("[CONCURRENT_CHUNKED] Finding top frequent words in a file - END");
        return retrieveTopFrequentWordsAndSortByDescendingOrder(wordCounts, frequency);
    }

    private void processMappedBuffer(MappedByteBuffer mapperBuffer, Map<String, Integer> wordCounts, long fileSize) throws FileReadingException {
        long position = 0;

        long remainingSizeToProc = 0;
        long bytesToRead = 0;

        int bufferCapacity = bufferSizeInBytes + maxChunkedWordLength;
        int actualBufferCapacity = 0;
        int overlapLength = 0;

        // Thread Pool
        ExecutorService executor = Executors.newFixedThreadPool(MAX_CONCURRENT_TASKS);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();


        while (position < fileSize && bufferCapacity > 0) {

            mapperBuffer.position((int) position);

            // Allocated with extra space for potential word overlap
            ByteBuffer buffer = ByteBuffer.allocate(bufferCapacity);

            remainingSizeToProc = fileSize - position;
            bytesToRead = Math.min(bufferSizeInBytes, remainingSizeToProc);
            actualBufferCapacity = Math.min(bufferCapacity, (int) remainingSizeToProc);


            // Getting chunks of data from source
            buffer.clear();
            byte[] temp = new byte[(int) actualBufferCapacity];
            mapperBuffer.get(temp, 0, actualBufferCapacity);
            buffer.put(temp, 0, actualBufferCapacity);
            buffer.flip();


            // Consider scenario where the last word gets cut-off from in the current chunk
            overlapLength = handleOverlappedWord(buffer, fileSize, position, bytesToRead, bufferCapacity, maxChunkedWordLength);


            // Add the chunk for processing
            futures.add(executor.submit(new ConcurrentMemoryMappedChunkingWordCounter.WordCountTask(buffer, wordCounts)));

            position += bytesToRead + overlapLength;
        }

        // Wait for all chunk processing tasks to complete
        for (Future<Map<String, Integer>> future : futures) {
            try {
                Map<String, Integer> chunkWordCounts = future.get();
                mergeWordCounts(wordCounts, chunkWordCounts);
            } catch (InterruptedException | ExecutionException e) {
                throw new FileReadingException("");
            }
        }

        executor.shutdown();
    }

    private void mergeWordCounts(Map<String, Integer> wordCounts, Map<String, Integer> chunkWordCounts) {
        // Merge the word counts from a chunk into the main wordCounts map
        for (Map.Entry<String, Integer> entry : chunkWordCounts.entrySet()) {
            wordCounts.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }



    private static class WordCountTask implements Callable<Map<String, Integer>> {
        private final ByteBuffer chunk;
        private final Map<String, Integer> wordCountMap;

        WordCountTask(ByteBuffer chunk, Map<String, Integer> wordCounts) {
            this.chunk = chunk;
            this.wordCountMap = wordCounts;
        }

        @Override
        public Map<String, Integer> call() {
            Map<String, Integer> chunkWordCounts = new HashMap<>();

            String content = new String(chunk.array(), chunk.position(), chunk.remaining(), StandardCharsets.UTF_8);
            String[] words = content.split(REG_EX_WORD_BOUNDARY);

            for (String word : words) {
                if (!word.isEmpty() && Character.isLetterOrDigit(word.charAt(0))) {

                    String cleanedWord = word.replaceAll(REG_EX_NON_ALPHABET_AND_NON_NUMBER, CommonConstants.EMPTY).toLowerCase(Locale.getDefault());

                    if (!cleanedWord.isEmpty()) {
                        wordCountMap.merge(cleanedWord, DEFAULT_COUNT_VALUE, Integer::sum);
                    }
                }
            }

            return chunkWordCounts;
        }
    }
}
