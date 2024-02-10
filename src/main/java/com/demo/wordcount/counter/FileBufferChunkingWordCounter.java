package com.demo.wordcount.counter;

import com.demo.wordcount.exception.FileReadingException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class FileBufferChunkingWordCounter extends ChunkingWordCounter {


    @Override
    public Map<String, Integer> retrieveTopFrequentWords(@NonNull Path sourceFilepath, int frequency) throws FileReadingException {
        log.info("[CHUNKED_IO] Finding top frequent words in a file - START");
        log.info(String.format("[CHUNKED] File: %s", sourceFilepath));

        Map<String, Integer> wordCountMap = new ConcurrentHashMap<>();

        try (FileChannel fileChannel = FileChannel.open(sourceFilepath, StandardOpenOption.READ)) {
            long fileSize = fileChannel.size();
            long position = 0;

            long remainingSizeToProc = 0;
            long bytesToRead = 0;

            int bufferCapacity = BUFFER_SIZE + MAX_WORD_LENGTH;
            int overlapLength = 0;

            while (position < fileSize) {
                ByteBuffer buffer = ByteBuffer.allocate(bufferCapacity); // Allocated with extra space for potential word overlap
                remainingSizeToProc = fileSize - position;
                bytesToRead = Math.min(BUFFER_SIZE, remainingSizeToProc);


                // Getting chunks of data from source
                buffer.clear();
                fileChannel.read(buffer, position);
                buffer.flip();


                // Considering scenario where the last word gets cut-off from in the current chunk
                overlapLength = handleOverlappedWord(buffer, fileSize, position, bytesToRead, bufferCapacity);


                // Processing of chunked data
                processBuffer(buffer, wordCountMap);


                position += bytesToRead + overlapLength;
            }
        } catch (IOException e) {
            log.error("Exception in reading the file contents", e);
            throw new FileReadingException(sourceFilepath.toString());
        }

        log.info("[CHUNKED] Finding top frequent words in a file - END");
        return retrieveTopFrequentWordsAndSortByDescendingOrder(wordCountMap, frequency);
    }

}
