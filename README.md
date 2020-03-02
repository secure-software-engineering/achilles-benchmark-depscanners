# Achilles - Benchmark for assessing OSS-Vulnerability Scanners 59

Achilles is an open test suite for evaluating the performance of open-source vulnerability scanners specifically for *Java* and *Maven* applications. 


## Achilles is the right benchmark for you, if you are
* Using open-source libraries and frameworks,
* Using the build-automation system *Maven* to manage project dependencies,
* Worried about (potential) vulnerabilities in the libraries you use,
* Comparing the performance of depenendy scanners,
* Deciding what tools to use for checking your open-source libraries for vulnerabilities,



## What is included



### Detection

The folder `detection` contains 2505 test fixtures to measure the performance of open-source vulnerability scanner for detecting the inclusion of known vulnerable dependencies.
The test fixtures are in the form of json files following the naming scheme: `<CVE>_<GAV>.json.`.

The test fixtures contain true and false positive warnings raised by the tools Vulas and OWASP Dependency-Check



### Generator
The generator creates, based on the test fixtures, Maven/Java projects that can be used as an input for open-source vulnerability scanner.
Each test fixtures specifies in the element `vulnerable` if the warnings is a true or false positive.



### Case Study




## We welcome your contributions!
You are most welcome to contribute additional test cases to Achilles. 
To do so, please fork the project, commit an appropriate test fixture, update this README and then send us a pull request.


### Tools to ease contributions