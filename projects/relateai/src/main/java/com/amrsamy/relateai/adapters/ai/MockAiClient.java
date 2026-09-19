package com.amrsamy.relateai.adapters.ai;

import com.amrsamy.relateai.application.port.AiClientPort;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.CRC32;

@Component
public class MockAiClient implements AiClientPort {

    private static final List<String> TEMPLATES = List.of(
            "Share one relationship habit that strengthened a connection this week — and why it worked.",
            "Describe a conversation you avoided. What would courage look like in that moment?",
            "Pick a topic you follow and write a 3-sentence invitation that would draw someone new in.",
            "Name a person you admire online. What specific behavior would you mirror today?",
            "Write a short note of appreciation to someone in your graph — no flattery, only evidence.",
            "What boundary would improve your relationships this month? State it as a clear request.",
            "Turn today's hottest topic into a question that sparks empathy rather than debate."
    );

    @Override
    public GeneratedChallenge generateDailyChallenge(String dateIso) {
        CRC32 crc = new CRC32();
        crc.update(dateIso.getBytes(StandardCharsets.UTF_8));
        int index = (int) (Math.abs(crc.getValue()) % TEMPLATES.size());
        String prompt = "Daily RelateAI Challenge (" + dateIso + "): " + TEMPLATES.get(index);
        return new GeneratedChallenge(prompt, "MOCK");
    }
}
