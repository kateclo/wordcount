package com.demo.wordcount.controller.response

import spock.lang.Specification

class ErrorTest extends Specification {
    def "should be able to create instance using parameterized constructor and retrieve values using getters" () {
        given:
        String messageVal = "message 101"
        int codeVal = -1

        when:
        Error error = new Error(codeVal, messageVal)

        then:
        error
        error.getCode() == -1
        error.getMessage() == "message 101"

    }


}
