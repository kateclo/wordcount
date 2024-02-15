package com.demo.wordcount.data.request


import com.demo.wordcount.exception.MinValueException
import com.demo.wordcount.exception.RequiredValueException
import com.demo.wordcount.exception.UnsupportedFileTypeException
import spock.lang.Specification
import spock.lang.Unroll


class WordCountRequestTest extends Specification {

    def "should be able to create instance using default constructor" () {
        when:
        WordCountRequest request = new WordCountRequest()

        then:
        request.source == null
        request.frequency == null
    }

    def "should be able to create instance using an all-args constructor" () {
        when:
        WordCountRequest request = new WordCountRequest("abc123", 789)

        then:
        request.source == "abc123"
        request.frequency == 789
    }

    def "should be able to set value using setters and retrieve using getters"() {
        given:
        WordCountRequest request = new WordCountRequest()

        when:
        request.setSource("some source value")
        request.setFrequency(123)

        then:
        request.getSource() == "some source value"
        request.getFrequency() == 123
    }

    @Unroll
    def "should be able to return #expResult when comparing the current request to a #scenario"() {
        given:
        WordCountRequest request = new WordCountRequest()
        request.setSource("abc.txt")
        request.setFrequency(50)


        when:
        boolean result = request.equals(newRequest)

        then:
        result == expResult

        where:
        scenario                           | newRequest                            || expResult
        "null request"                     | null                                  || false
        "request with different source"    | new WordCountRequest("abc.png", 50)   || false
        "request with different frequency" | new WordCountRequest("abc.txt", 0)    || false
        "request with null source"         | new WordCountRequest(null, 50)        || false
        "request with null frequency"      | new WordCountRequest("abc.txt", null) || false
        "request with same values"         | new WordCountRequest("abc.txt", 50)   || true

    }

    @Unroll
    def "should be able to return #expResult when comparing the current request (with null source) to a #scenario"() {
        given:
        WordCountRequest request = new WordCountRequest()
        request.setSource(null)
        request.setFrequency(50)


        when:
        boolean result = request.equals(newRequest)

        then:
        result == expResult

        where:
        scenario                           | newRequest                            || expResult
        "null request"                     | null                                  || false
        "request with different source"    | new WordCountRequest("abc.png", 50)   || false
        "request with different frequency" | new WordCountRequest("abc.txt", 0)    || false
        "request with null source"         | new WordCountRequest(null, 50)        || true
        "request with null frequency"      | new WordCountRequest("abc.txt", null) || false
    }


    def "should be able to return true when comparing the current request to itself"() {
        given:
        WordCountRequest request = new WordCountRequest()
        request.setSource("abc.txt")
        request.setFrequency(50)


        when:
        boolean result = request.equals(request)

        then:
        result
    }




    @Unroll
    def "should throw an exception when validating request and #scenario"() {
        given:
        WordCountRequest request = new WordCountRequest()

        and:
        request.setSource(paramSource)
        request.setFrequency(paramKvalue)

        when:
        request.validate()

        then:
        thrown result

        where:
        scenario                   | paramSource | paramKvalue || result
        "source is null"           | null        | 10          || RequiredValueException
        "source is empty"          | ""          | 10          || RequiredValueException
        "source is empty - 2"      | "      "    | 10          || RequiredValueException
        "source is empty - 3"      | "\n\t\n"    | 10          || RequiredValueException
        "source is not a txt file" | "abc.png"   | 10          || UnsupportedFileTypeException

        "frequency is null"        | "abc.txt"   | null        || MinValueException
        "frequency is lt 0"        | "abc.txt"   | -5          || MinValueException
        "frequency is 0"           | "abc.txt"   | 0           || MinValueException

    }

    @Unroll
    def "should throw no exception when validating request and #scenario"() {
        given:
        WordCountRequest request = new WordCountRequest()

        and:
        request.setSource(paramSource)
        request.setFrequency(paramKvalue)

        when:
        request.validate()

        then:
        noExceptionThrown()


        where:
        scenario               | paramSource         | paramKvalue || result
        "source is a txt file" | "C:/abc/sample.txt" | 10          || null
        "frequency is gt 0"    | "abc.txt"           | 1           || null

    }


}
