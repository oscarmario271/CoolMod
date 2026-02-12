package bosha.coolmod.client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class PublicAccountService {

    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final class AccountRecord {
        String username;
        String password;
        long mineboshaMoney;
    }

    private static final class DatabaseModel {
        AccountRecord[] accounts;
    }

    private static final Map<String, AccountRecord> CACHE = new HashMap<>();
    private static String loggedInUsername = null;

    private PublicAccountService() {
    }

    public static boolean refresh() {
        try {
            String url = CoolModConfig.mineboshaPublicJsonUrl == null
                    ? ""
                    : CoolModConfig.mineboshaPublicJsonUrl.trim();
            if (url.isEmpty()) {
                return false;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return false;
            }

            DatabaseModel model = GSON.fromJson(response.body(), DatabaseModel.class);
            CACHE.clear();

            if (model == null || model.accounts == null) {
                return true;
            }

            for (AccountRecord record : model.accounts) {
                if (record == null || record.username == null || record.password == null) {
                    continue;
                }
                String key = normalize(record.username);
                if (!key.isBlank()) {
                    CACHE.put(key, record);
                }
            }
            return true;
        } catch (IllegalArgumentException | JsonSyntaxException ignored) {
            return false;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean login(String username, String password) {
        String key = normalize(username);
        AccountRecord record = CACHE.get(key);
        if (record == null || !record.password.equals(password)) {
            return false;
        }
        loggedInUsername = key;
        return true;
    }

    public static void logout() {
        loggedInUsername = null;
    }

    public static boolean isLoggedIn() {
        return loggedInUsername != null;
    }

    public static String getLoggedInUsername() {
        if (!isLoggedIn()) {
            return null;
        }
        AccountRecord record = CACHE.get(loggedInUsername);
        return record == null ? null : record.username;
    }

    public static long getMineboshaMoney() {
        if (!isLoggedIn()) {
            return 0L;
        }
        AccountRecord record = CACHE.get(loggedInUsername);
        return record == null ? 0L : Math.max(0L, record.mineboshaMoney);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
