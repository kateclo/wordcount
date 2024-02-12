package com.demo.wordcount.data.response

import com.demo.wordcount.data.response.WordCountDetails
import spock.lang.Specification

class WordCountDetailsTest extends Specification {
    def "should be able to create instance using default constructor" () {
        when:
        WordCountDetails details = new WordCountDetails()

        then:
        details
        details.count == 0
        details.word == null
    }

    def "should be able to create instance using parameterized constructor" () {
        given:
        String word = "snow"
        int count = 50

        when:
        WordCountDetails details = new WordCountDetails(word, count)

        then:
        details.count == 50
        details.word == "snow"
    }

    def "should be able to set value using setters and retrieve using getters"() {
        given:
        WordCountDetails details = new WordCountDetails()

        when:
        details.setCount(123)
        details.setWord("counter")

        then:
        details.getCount() == 123
        details.getWord() == "counter"
    }
}
