package com.amrsamy.relateai.application.port;

public interface AiClientPort {

    GeneratedChallenge generateDailyChallenge(String dateIso);

    record GeneratedChallenge(String prompt, String source) {}
}
