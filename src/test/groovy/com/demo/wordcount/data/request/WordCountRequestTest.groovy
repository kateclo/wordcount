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
