package software.bigbade.playervaults.loading;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class DatabaseSettings {
    private final String url;
    private final String username;
    private final String password;
    private final String database;
    private final String security;
    private final String tableName;
}
