package com.demo.wordcount.counter;

import java.nio.ByteBuffer;


public abstract class ChunkingWordCounter extends WordCounter {

    protected int handleOverlappedWord(ByteBuffer buffer,
                                       long fileSize,
                                       long position,
                                       long bytesToRead,
                                       int bufferCapacity,
                                       int maxChunkedWordLength) {
        int overlapLength = 0;
        if (position + bufferCapacity <= fileSize) {
            overlapLength = getOverlapLength(buffer, bytesToRead, maxChunkedWordLength);

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


    private int getOverlapLength(ByteBuffer buffer, long bytesToRead, int maxChunkedWordLength) {
        int overlapLength = 0;

        // Start reading from end of the current chunk's expected size (not the buffer's extended capacity).
        buffer.position((int) bytesToRead);

        // Get the overlap length of the last word in the current chunk.
        // This is the length that went over the next chunk.
        while (buffer.hasRemaining() && overlapLength < maxChunkedWordLength) {
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
