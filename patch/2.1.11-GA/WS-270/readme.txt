Summary

    * Status: Replace TimeZone.getTimeZone with the util method from the kernel to have less contention
    * CCP Issue: CCP-1118, Product Jira Issue: WS-270.
    * Complexity: Low

The Proposal
Problem description

What is the problem to fix?
Under heavy load we realized that the method java.util.TimeZone.getTimeZone(String ID) becomes a bottleneck so it is necessary to use the alternative provided by the kernel to have less contention.

Fix description

How is the problem fixed?
* Avoided usage of method java.util.TimeZone.getTimeZone(String ID), now we use less synchronized method implemented in kernel project instead.

Patch file: WS-270.patch

Tests to perform

Reproduction test

    * Activate the Concurrent GC during run of benchmark

Tests performed at DevLevel

    * Functional testing for all projects (kernel, core, ws, jcr)

Tests performed at QA/Support Level
*
Documentation changes

Documentation changes:

    * No

Configuration changes

Configuration changes:

    * No

Will previous configuration continue to work?

    * Yes

Risks and impacts

Can this bug fix have any side effects on current client projects?

    * No

Is there a performance risk/cost?

    * No

Validation (PM/Support/QA)

PM Comment
* Patch validated.

Support Comment
*

QA Feedbacks
*
