package de.newsh;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import haven.Config;

public class TelegramHandler {

	public static void updateTimerBot(String timerName, long timerDuration, boolean addJob) {

		JsonParser parser = new JsonParser();

		try {
			String telegramChatID = Config.loadFile("telegramBot.json");

			Object obj = parser.parse(telegramChatID);

			JsonObject jsonObject = (JsonObject) obj;
			JsonElement name = jsonObject.get("chatID");
			telegramChatID = name.getAsString();

			if (Pattern.matches("[0-9,-]+", telegramChatID)) { // chatID must only contain numbers. '-' at start indicates groupChat

				String jsonString = "{\"TimerName\":\"" + timerName + "\",\"Duration\":\"" + timerDuration + "\",\"ChatID\":\"" + telegramChatID + "\"}"; // build json String to send to script
				try {
					jsonString = URLEncoder.encode(jsonString, "UTF-8"); // Url encode, so special characters used in name don't break url

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				HttpClient httpClient = new DefaultHttpClient();

				try {
					HttpPost request = null;
					if (addJob)
						request = new HttpPost("http://newsh.de/hnh/addJob.php");
					else
						request = new HttpPost("http://newsh.de/hnh/deleteJob.php");

					StringEntity params = new StringEntity("message=" + jsonString);
					request.addHeader("content-type", "application/x-www-form-urlencoded");
					request.addHeader("charset", "utf-8");
					request.setEntity(params);
					httpClient.execute(request); // send json data to script
				} catch (Exception ex) {
				} finally {
					httpClient.getConnectionManager().shutdown();
				}
			}
		} catch (Exception e) {
		}

	}

}
