# Achilles - Test Suite for assessing OSS-Vulnerability Scanners

Achilles is an open test suite for evaluating the performance of open-source vulnerability scanners specifically for *Java* and *Maven* applications. 


Achilles was created by Andreas Dann (1), Henrik Plate (2), Ben Hermann (3), Serena Elisa (2) Ponta,  and Eric Bodden (1) of the security research group at Paderborn University (1),  SAP Security Research Mougins, France (2), and the secure software engineering group at Technical University Dortmund (3).

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

The test fixtures contain true and false positive warnings raised by the tools [Eclipse Steady](https://github.com/eclipse/steady), [OWASP Dependency-Check](https://github.com/jeremylong/DependencyCheck), and a commercial open-source dependency vulnerability scanner.



### Generator
The generator creates, based on the test fixtures, Maven/Java projects that can be used as an input for open-source vulnerability scanner.
Each test fixtures specifies in the element `vulnerable` if the warnings is a true or false positive.



<p align="center">
  <img width="75%" src="achilles_overview_testcases.png">
</p>




## We welcome your contributions!
You are most welcome to contribute additional test cases to Achilles. 
To do so, please fork the project, commit an appropriate test fixture, update this README and then send us a pull request.



## License
Achilles is licensed under the LGPLv3 license, see LICENSE file. This basically means that you are free to use the tool (even in commercial, closed-source projects). However, if you extend or modify the tool, you must make your changes available under the LGPLv3 as well. This ensures that we can continue to improve the tool as a community effort.


### Contact

If you experience any issues, you can ask for help on GitHub issue board. You can also contact us at andreas.dann@uni-paderborn.de


