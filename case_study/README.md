# achilles_testcases


## Evaluation
Which test cases (feature combinations and vulnerabilities) shall we generate?

1) original GAV, with md, single jar, original classes
2) modified GAV, with md (the modified), single jar, recompiled
3) random GAV, with md, uber jar, original classes (java stand-alone, self-contained case)
4) random GAV, without md, uber jar, original classes (java stand-alone, self-contained case)
5) random GAV, with md, uber jar, re-packaged classes (agent case, to avoid conflicts with instrumented app)
2-5) * 2 (with vulnerable code where known, with all code for other archives), (without vulnerable code where known, with all code for other archives)
