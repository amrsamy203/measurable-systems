package com.amrsamy.caseflow.application.port;

public interface CasePipelinePort {
    void processIngest(Long caseId);

    void processAssignment(Long caseId);
}
