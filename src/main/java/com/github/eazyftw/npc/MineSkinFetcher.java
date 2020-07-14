package com.github.eazyftw.npc;

import com.github.eazyftw.npc.profile.Profile;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MineSkinFetcher {

    private static final String MINESKIN_API = "https://api.mineskin.org/get/id/";

    public static Profile.Property fetchSkinFromId(int id) {
        try {
            StringBuilder builder = new StringBuilder();
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(MINESKIN_API + id).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            Scanner scanner = new Scanner(httpURLConnection.getInputStream());
            while (scanner.hasNextLine()) builder.append(scanner.nextLine());

            scanner.close();
            httpURLConnection.disconnect();

            JsonObject jsonObject = (JsonObject) new JsonParser().parse(builder.toString());
            JsonObject textures = jsonObject.get("data").getAsJsonObject().get("texture").getAsJsonObject();
            String value = textures.get("value").getAsString();
            String signature = textures.get("signature").getAsString();

            return new Profile.Property("textures", value, signature);
        } catch (Exception exception) {
            Bukkit.getLogger().severe("Could not fetch skin! (Id: " + id + "). Message: " + exception.getMessage());
            exception.printStackTrace();
            return null;
        }
    }
}
