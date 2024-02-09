package com.demo.wordcount.counter;

import com.demo.wordcount.exception.FileReadingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MemoryMappedChunkingWordCounter extends ChunkingWordCounter {

    @Override
    public Map<String, Integer> retrieveTopKWords(Path sourceFilepath, int k) throws FileReadingException {
        log.info("[MEM_MAPPED] Finding top K words in a file - START");
        log.info(String.format("[MEM_MAPPED] File: %s", sourceFilepath));

        ConcurrentHashMap<String, Integer> wordCountMap = new ConcurrentHashMap<>();

        try (FileChannel fileChannel = FileChannel.open(sourceFilepath, StandardOpenOption.READ)) {
            long fileSize = fileChannel.size();

            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);

            processMappedBuffer(buffer, wordCountMap, fileSize);

        } catch (IOException | SecurityException | IndexOutOfBoundsException e) {
            throw new FileReadingException(sourceFilepath.toString());
        }


        log.info("[MEM_MAPPED] Finding top K words in a file - END");
        return retrieveTopKWordsAndSortByDescendingOrder(wordCountMap, k);
    }

    private void processMappedBuffer(MappedByteBuffer mapperBuffer, Map<String, Integer> wordCountMap, long fileSize) {
        long position = 0;

        long remainingSizeToProc = 0;
        long bytesToRead = 0;

        int bufferCapacity = BUFFER_SIZE + MAX_WORD_LENGTH;
        int actualBufferCapacity = 0;
        int overlapLength = 0;

        while (position < fileSize) {

            mapperBuffer.position((int) position);

            ByteBuffer buffer = ByteBuffer.allocate(bufferCapacity); // Allocated with extra space for potential word overlap

            remainingSizeToProc = fileSize - position;
            bytesToRead = Math.min(BUFFER_SIZE, remainingSizeToProc);
            actualBufferCapacity = Math.min(bufferCapacity, (int) remainingSizeToProc);


            // Getting chunks of data from source
            buffer.clear();
            byte[] temp = new byte[(int) actualBufferCapacity];
            mapperBuffer.get(temp, 0, actualBufferCapacity);
            buffer.put(temp, 0, actualBufferCapacity);
            buffer.flip();


            // Considering scenario where the last word gets cut-off from in the current chunk
            overlapLength = handleOverlappedWord(buffer, fileSize, position, bytesToRead, bufferCapacity);


            // Processing of chunked data
            processBuffer(buffer, wordCountMap);


            position += bytesToRead + overlapLength;
        }
    }


}
