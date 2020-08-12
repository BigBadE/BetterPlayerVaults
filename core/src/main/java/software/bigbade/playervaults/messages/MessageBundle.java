package software.bigbade.playervaults.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MessageBundle {
    @Getter
    private final String language;
    private final Map<String, String> messages = new HashMap<>();

    public void addMessage(String key, String message) {
        messages.put(key, message);
    }

    public String getMessage(String key) {
        return messages.get(key);
    }
}
