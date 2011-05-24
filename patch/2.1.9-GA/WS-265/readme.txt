Summary

    Status: Stop the StandaloneContainer on context destroyed
    CCP Issue: N/A, Product Jira Issue: WS-265 fixed also WS-264
    Complexity: Low

The Proposal
Problem description

What is the problem to fix?
Problem of WS-264
Patch StandaloneContainerInitializedListener to get it works with relative path configured as system property

exo.ws.frameworks.servlet/src/main/java/org/exoplatform/ws/frameworks/servlet/StandaloneContainerInitializedListener.java

Problem of WS-265
To be able to stop all the services, it is need to call container.stop on contextDestroyed of StandaloneContainerInitializedListener.
Fix description

How is the problem fixed?

* Stop the StandaloneContainer on context destroyed

     Allow to set configuration from relative path

Patch information:
WS-265.patch

Tests to perform

Reproduction test
  * No

Tests performed at DevLevel
  * Functional testing in ws project

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
    No

Is there a performance risk/cost?
  * No

Validation (PM/Support/QA)

PM Comment
* PL review: Patch validated

Support Comment
* Support review: Patch validated

QA Feedbacks
*

