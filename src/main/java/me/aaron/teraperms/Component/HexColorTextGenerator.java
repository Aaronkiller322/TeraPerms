package me.aaron.teraperms.Component;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HexColorTextGenerator {

	private static ChatColor interpolateColor(String startColor, String endColor, double ratio) {
		int startRGB = Integer.parseInt(startColor.substring(1), 16);
		int endRGB = Integer.parseInt(endColor.substring(1), 16);

		int interpolatedRGB = interpolateRGB(startRGB, endRGB, ratio);
		String interpolatedHex = String.format("#%06X", (0xFFFFFF & interpolatedRGB));

		return ChatColor.of(interpolatedHex);
	}

	private static int interpolateRGB(int startRGB, int endRGB, double ratio) {
		int startRed = (startRGB >> 16) & 0xFF;
		int startGreen = (startRGB >> 8) & 0xFF;
		int startBlue = startRGB & 0xFF;

		int endRed = (endRGB >> 16) & 0xFF;
		int endGreen = (endRGB >> 8) & 0xFF;
		int endBlue = endRGB & 0xFF;

		int interpolatedRed = (int) (startRed + ratio * (endRed - startRed));
		int interpolatedGreen = (int) (startGreen + ratio * (endGreen - startGreen));
		int interpolatedBlue = (int) (startBlue + ratio * (endBlue - startBlue));

		return (interpolatedRed << 16) | (interpolatedGreen << 8) | interpolatedBlue;
	}

	public static String converHexcolorText(String text, String start_hexcolor, String end_hexcolor) {
		if (text == null || text.isEmpty()) {
		    return "";
		}
		String[] split = text.split("");

		int steps = split.length;

		TextComponent end_text = new TextComponent("");
		for (int i = 0; i <= steps; i++) {
			try {
				if (split[i] != null) {

					double ratio = (double) i / steps;
					ChatColor color = interpolateColor(start_hexcolor, end_hexcolor, ratio);

					TextComponent message = new TextComponent(split[i]);
					message.setColor(color);
					end_text.addExtra(message);
				}
			} catch (Exception e) {
			}
		}

		return end_text.toLegacyText();

	}

	public static String convertHexColorToChatColor(String msg) {
		Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
		Matcher match = pattern.matcher(msg);
		while (match.find()) {
			String color = msg.substring(match.start(), match.end());
			msg = msg.replace(color, ChatColor.of(color) + "");
			match = pattern.matcher(msg);

		}
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	public static String convertAllInfo(String message) {
		String msg = message.replace("%prefix%", PrefixManager.getPrefix());
		return convertHexColorToChatColor(msg);
	}


}
