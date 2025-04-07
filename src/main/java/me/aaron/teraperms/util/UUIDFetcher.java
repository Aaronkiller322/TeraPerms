package me.aaron.teraperms.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class UUIDFetcher {

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)"
    );

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String NAME_URL = "https://api.mojang.com/user/profile/%s";

    private static final ConcurrentHashMap<String, UUID> uuidCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, String> nameCache = new ConcurrentHashMap<>();

    public static CompletableFuture<UUID> getUUIDAsync(String name) {
        if (name == null) return CompletableFuture.completedFuture(null);
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            UUID uuid = player.getUniqueId();
            uuidCache.put(name.toLowerCase(), uuid);
            nameCache.put(uuid, player.getName());
            return CompletableFuture.completedFuture(uuid);
        }
        String lowerName = name.toLowerCase();
        if (uuidCache.containsKey(lowerName)) {
            return CompletableFuture.completedFuture(uuidCache.get(lowerName));
        }
        return fetchFromAPI(String.format(UUID_URL, lowerName))
                .thenComposeAsync(UUIDFetcher::readAllAsync)
                .thenComposeAsync(UUIDFetcher::parseUUIDAsync)
                .exceptionally(ex -> {
                    return null;
                });
    }

    public static CompletableFuture<String> getNameAsync(UUID uuid) {
        if (uuid == null) return CompletableFuture.completedFuture(null);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            String name = player.getName();
            uuidCache.put(name.toLowerCase(), uuid);
            nameCache.put(uuid, name);
            return CompletableFuture.completedFuture(name);
        }
        if (nameCache.containsKey(uuid)) {
            return CompletableFuture.completedFuture(nameCache.get(uuid));
        }
        return formatUUIDAsync(uuid.toString()).thenComposeAsync(formattedUUID ->
                fetchFromAPI(String.format(NAME_URL, formattedUUID.replace("-", "")))
                        .thenComposeAsync(UUIDFetcher::readAllAsync)
                        .thenComposeAsync(UUIDFetcher::parseNameAsync)
                        .exceptionally(ex -> {
                            return null;
                        })
        );
    }

    private static CompletableFuture<HttpURLConnection> fetchFromAPI(String urlString) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                return connection;
            } catch (IOException e) {
                return null;
            }
        });
    }

    private static CompletableFuture<String> readAllAsync(HttpURLConnection connection) {
        return CompletableFuture.supplyAsync(() -> {
            if (connection == null) return null;
            try (InputStream inputStream = connection.getInputStream()) {
                return readAll(inputStream);
            } catch (IOException e) {
                return null;
            }
        });
    }

    private static CompletableFuture<UUID> parseUUIDAsync(String response) {
        return CompletableFuture.supplyAsync(() -> {
            if (response == null || response.isEmpty()) return null;
            try {
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                return formatUUIDAsync(jsonObject.get("id").getAsString()).thenApply(id -> {
                    UUID uuid = UUID.fromString(id);
                    String name = jsonObject.get("name").getAsString();
                    uuidCache.put(name.toLowerCase(), uuid);
                    nameCache.put(uuid, name);
                    return uuid;
                }).join();
            } catch (Exception e) {
                return null;
            }
        });
    }

    private static CompletableFuture<String> parseNameAsync(String response) {
        return CompletableFuture.supplyAsync(() -> {
            if (response == null || response.isEmpty()) return null;
            try {
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                return formatUUIDAsync(jsonObject.get("id").getAsString()).thenApply(id -> {
                    UUID fetchedUUID = UUID.fromString(id);
                    String name = jsonObject.get("name").getAsString();
                    uuidCache.put(name.toLowerCase(), fetchedUUID);
                    nameCache.put(fetchedUUID, name);
                    return name;
                }).join();
            } catch (Exception e) {
                return null;
            }
        });
    }

    private static CompletableFuture<String> formatUUIDAsync(String id) {
        return CompletableFuture.supplyAsync(() ->
                UUID_PATTERN.matcher(id).replaceFirst("$1-$2-$3-$4-$5")
        );
    }

    private static String readAll(InputStream inputStream) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toString(StandardCharsets.UTF_8.name());
        }
    }
}