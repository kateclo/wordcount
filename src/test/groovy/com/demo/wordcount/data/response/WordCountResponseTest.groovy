package com.demo.wordcount.data.response

import com.demo.wordcount.data.response.WordCountDetails
import com.demo.wordcount.data.response.WordCountResponse
import spock.lang.Specification
import spock.lang.Unroll

class WordCountResponseTest extends Specification {

    @Unroll
    def "should be able to create instance using parameterized constructor when #scenario" () {
        when:
        WordCountResponse response = new WordCountResponse(inputData)

        then:
        response
        response.getWordCountDetails().size() == size

        if (response.getWordCountDetails().size() == 1) {
            response.getWordCountDetails().get(0).word == "abc"
            response.getWordCountDetails().get(0).count == 5
        }


        where:
        scenario                 | inputData                                    || size
        "null list"              | null                                         || 0
        "empty list"             | []                                           || 0
        "list has null entries"  | [null, null, null]                           || 0
        "list has entries"       | [new WordCountDetails("abc", 5)] || 1
        "list has mixed entries" | [null, new WordCountDetails("abc", 5), null] || 1

    }
}
