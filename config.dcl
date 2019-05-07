// This is a comment.
string: "» sea1.stacktrace.fun"
integer: 6379
double: 1.01
booleanValue: true

# This is also a comment.
redis {
    uri: "sea1.stacktrace.fun"
    port: 6379
    password: "yay"
}

anotherValue: "this is fun!"

anotherSection {
    value: "1"

    embeddedSection {
        value: "2"

        anotherEmbeddedSection {
            value: "3"
        }
    }
}
