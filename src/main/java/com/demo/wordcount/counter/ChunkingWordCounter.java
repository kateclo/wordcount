package com.demo.wordcount.counter;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class ChunkingWordCounter extends WordCounter {

    protected static final int BUFFER_SIZE = 1024 * 1024; // 1 MB chunk size

    protected static final int MAX_WORD_LENGTH = 100;

    protected int handleOverlappedWord(ByteBuffer buffer, long fileSize, long position, long bytesToRead, int bufferCapacity) {
        int overlapLength = 0;
        if (position + bufferCapacity < fileSize) {
            overlapLength = getOverlapLength(buffer, bytesToRead);

            if (overlapLength > 0) {
                // new buffer limit
                buffer.limit((int) bytesToRead + overlapLength);
            } else {
                buffer.limit((int) bytesToRead);
            }

            buffer.flip();
        }
        return overlapLength;
    }


    // Process the buffer data, which has considered overlapped length
    protected void processBuffer(ByteBuffer buffer, Map<String, Integer> wordCountMap) {
        String content = new String(buffer.array(), buffer.position(), buffer.remaining(), StandardCharsets.UTF_8);
        countUniqueAndValidWords(wordCountMap, content);
    }

    private int getOverlapLength(ByteBuffer buffer, long bytesToRead) {
        int overlapLength = 0;

        // Start reading from end of the current chunk's expected size (not the buffer's extended capacity).
        buffer.position((int) bytesToRead);

        // Get the overlap length of the last word in the current chunk.
        // This is the length that went over the next chunk.
        while (buffer.hasRemaining() && overlapLength < MAX_WORD_LENGTH) {
            byte currentByte = buffer.get();
            if (Character.isWhitespace((char) currentByte)) {
                break;
            } else {
                // Found a non-whitespace character
                overlapLength++;
            }

        }

        return overlapLength;
    }


}
