package com.demo.wordcount.counter;

import com.demo.wordcount.exception.UnsupportedCounterModeException;

public class WordCounterFactory {

    public static WordCounter getWordCounter(String mode) throws UnsupportedCounterModeException {
        if (WordCounter.Mode.CONCURRENT.name().equalsIgnoreCase(mode)) {
            return new ConcurrentFileBufferWordCounter();
        } else if (WordCounter.Mode.CHUNKED.name().equalsIgnoreCase(mode)) {
            return new FileBufferChunkingWordCounter();
        } else if (WordCounter.Mode.MEM_MAPPED.name().equalsIgnoreCase(mode)) {
            return new MemoryMappedChunkingWordCounter();
        } else if (WordCounter.Mode.CONCURRENT_MEM_MAPPED.name().equalsIgnoreCase(mode)) {
            return new ConcurrentMemoryMappedChunkingWordCounter();
        } else {
            throw new UnsupportedCounterModeException(mode);
        }
    }
}
