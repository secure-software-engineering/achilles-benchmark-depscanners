# Achilles
Achilles- A corpus of vulnerable OSS


## Detection

The folder `detection` contains 2505 test fixtures to measure the performance of open-source vulnerability scanner for detecting the inclusion of known vulnerable dependencies.
The test fixtures are in the form of json files following the naming scheme: `<CVE>_<GAV>.json.`.

The test fixtures contain true and false positive warnings raised by the tools Vulas and OWASP Dependency-Check



### Generator
The generator creates, based on the test fixtures, Maven/Java projects that can be used as an input for open-source vulnerability scanner.
Each test fixtures specifies in the element `vulnerable` if the warnings is a true or false positive.



## Reachability
