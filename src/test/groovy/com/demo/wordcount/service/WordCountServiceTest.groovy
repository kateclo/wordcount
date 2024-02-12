package com.demo.wordcount.service

import spock.lang.Specification

class WordCountServiceTest extends Specification {
    WordCountService wordCountService

    def setup() {
        wordCountService = new WordCountService()
        wordCountService.downloadsDir = "downloads"
        wordCountService.counterMode = "CONCURRENT_MEM_MAPPED"

    }



    def "should throw IllegalArgumentException if request has validation error"() {

    }

}
