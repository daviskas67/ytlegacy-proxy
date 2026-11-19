package com.ytlegacy.proxy;

import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ChannelData {
    private static final String TAG = "ChannelData";

    public String id = "";
    public String title = "";
    public String description = "";
    public long subscriberCount = 0;
    public long videoCount = 0;
    public String avatarUrl = "";
    public String bannerUrl = "";
    public String vanityUrl = "";
    public List<VideoData> videos = new ArrayList<>();

    /** Converts the public Invidious channel API response to legacy channel metadata. */
    public static ChannelData fromInvidious(JsonObject resp, String fallbackId) {
        ChannelData c = new ChannelData();
        if (resp == null) return c;
        c.id = VideoData.getStr(resp, "authorId", fallbackId);
        c.title = VideoData.getStr(resp, "author", "YouTube");
        c.description = VideoData.getStr(resp, "description", "");
        try {
            if (resp.has("subCount") && !resp.get("subCount").isJsonNull())
                c.subscriberCount = resp.get("subCount").getAsLong();
        } catch (Exception ignored) {}
        try {
            if (resp.has("videoCount") && !resp.get("videoCount").isJsonNull())
                c.videoCount = resp.get("videoCount").getAsLong();
        } catch (Exception ignored) {}

        if (resp.has("authorThumbnails") && resp.get("authorThumbnails").isJsonArray()) {
            JsonArray thumbs = resp.getAsJsonArray("authorThumbnails");
            if (thumbs.size() > 0) {
                JsonObject last = thumbs.get(thumbs.size() - 1).getAsJsonObject();
                c.avatarUrl = VideoData.getStr(last, "url", "");
            }
        }
        if (resp.has("authorBanners") && resp.get("authorBanners").isJsonArray()) {
            JsonArray banners = resp.getAsJsonArray("authorBanners");
            if (banners.size() > 0) {
                JsonObject last = banners.get(banners.size() - 1).getAsJsonObject();
                c.bannerUrl = VideoData.getStr(last, "url", "");
            }
        }
        if (resp.has("latestVideos") && resp.get("latestVideos").isJsonArray()) {
            JsonArray videos = resp.getAsJsonArray("latestVideos");
            for (int i = 0; i < videos.size(); i++) {
                try {
                    VideoData video = VideoData.fromInvidious(videos.get(i).getAsJsonObject());
                    if (video != null) c.videos.add(video);
                } catch (Exception ignored) {}
            }
        }
        return c;
    }

    public static ChannelData fromBrowse(JsonObject resp, String channelId) {
        ChannelData c = new ChannelData();
        c.id = channelId;
        if (resp == null) {
            Log.w(TAG, "fromBrowse: resp is null for " + channelId);
            return c;
        }

        // ==== 1. HEADER: c4TabbedHeaderRenderer ====
        try {
            if (resp.has("header") && resp.get("header").isJsonObject()) {
                JsonObject header = resp.getAsJsonObject("header");
                if (header.has("c4TabbedHeaderRenderer")) {
                    JsonObject c4 = header.getAsJsonObject("c4TabbedHeaderRenderer");

                    c.title = VideoData.getStr(c4, "title", "");
                    String chId = VideoData.getStr(c4, "channelId", "");
                    if (!chId.isEmpty()) c.id = chId;

                    // Avatar
                    if (c4.has("avatar") && c4.get("avatar").isJsonObject()) {
                        JsonObject av = c4.getAsJsonObject("avatar");
                        if (av.has("thumbnails") && av.get("thumbnails").isJsonArray()) {
                            JsonArray th = av.getAsJsonArray("thumbnails");
                            if (th.size() > 0) {
                                JsonObject last = th.get(th.size() - 1).getAsJsonObject();
                                c.avatarUrl = VideoData.getStr(last, "url", "");
                            }
                        }
                    }

                    // Banner
                    if (c4.has("banner") && c4.get("banner").isJsonObject()) {
                        JsonObject b = c4.getAsJsonObject("banner");
                        if (b.has("thumbnails") && b.get("thumbnails").isJsonArray()) {
                            JsonArray th = b.getAsJsonArray("thumbnails");
                            if (th.size() > 0) {
                                JsonObject first = th.get(0).getAsJsonObject();
                                c.bannerUrl = VideoData.getStr(first, "url", "");
                            }
                        }
                    }

                    // Subscribers
                    if (c4.has("subscriberCountText") && c4.get("subscriberCountText").isJsonObject()) {
                        c.subscriberCount = VideoData.parseNumber(
                            VideoData.getRunsText(c4.getAsJsonObject("subscriberCountText")));
                    }

                    // Videos count
                    if (c4.has("videosCountText") && c4.get("videosCountText").isJsonObject()) {
                        c.videoCount = VideoData.parseNumber(
                            VideoData.getRunsText(c4.getAsJsonObject("videosCountText")));
                    }
                }
            }
        } catch (Exception e) { Log.e(TAG, "header error: " + e); }

        // ==== 2. TABS: singleColumnBrowseResultsRenderer.tabs[] ====
        try {
            if (resp.has("contents") && resp.get("contents").isJsonObject()) {
                JsonObject contents = resp.getAsJsonObject("contents");
                JsonObject tabsRoot = null;
                if (contents.has("singleColumnBrowseResultsRenderer")) {
                    tabsRoot = contents.getAsJsonObject("singleColumnBrowseResultsRenderer");
                } else if (contents.has("twoColumnBrowseResultsRenderer")) {
                    tabsRoot = contents.getAsJsonObject("twoColumnBrowseResultsRenderer");
                }

                if (tabsRoot != null && tabsRoot.has("tabs") && tabsRoot.get("tabs").isJsonArray()) {
                    JsonArray tabs = tabsRoot.getAsJsonArray("tabs");
                    Log.i(TAG, "Channel " + channelId + ": " + tabs.size() + " tabs");

                    for (int i = 0; i < tabs.size(); i++) {
                        JsonObject tab = tabs.get(i).getAsJsonObject();
                        if (!tab.has("tabRenderer")) continue;
                        JsonObject tr = tab.getAsJsonObject("tabRenderer");
                        String tabTitle = VideoData.getStr(tr, "title", "");
                        if (!tr.has("content") || !tr.get("content").isJsonObject()) continue;
                        JsonObject content = tr.getAsJsonObject("content");

                        Log.d(TAG, "  tab[" + i + "] '" + tabTitle + "' content keys: " + content.entrySet());

                        // Grid tabs: Videos, Playlists
                        if (content.has("gridRenderer")) {
                            JsonObject grid = content.getAsJsonObject("gridRenderer");
                            if (grid.has("items") && grid.get("items").isJsonArray()) {
                                JsonArray items = grid.getAsJsonArray("items");
                                for (int j = 0; j < items.size(); j++) {
                                    addVideo(items.get(j).getAsJsonObject(), c);
                                }
                            }
                        }

                        // Rich grid: Home
                        if (content.has("richGridRenderer")) {
                            JsonObject grid = content.getAsJsonObject("richGridRenderer");
                            if (grid.has("contents") && grid.get("contents").isJsonArray()) {
                                JsonArray items = grid.getAsJsonArray("contents");
                                for (int j = 0; j < items.size(); j++) {
                                    addVideo(items.get(j).getAsJsonObject(), c);
                                }
                            }
                        }

                        // Section list: Home / Videos
                        if (content.has("sectionListRenderer")) {
                            JsonObject sl = content.getAsJsonObject("sectionListRenderer");
                            if (sl.has("contents") && sl.get("contents").isJsonArray()) {
                                JsonArray sections = sl.getAsJsonArray("contents");
                                for (int j = 0; j < sections.size(); j++) {
                                    walkSection(sections.get(j).getAsJsonObject(), c);
                                }
                            }
                        }

                        // Item section (на случай если прямо в content)
                        if (content.has("itemSectionRenderer")) {
                            walkSection(content, c);
                        }
                    }
                }
            }
        } catch (Exception e) { Log.e(TAG, "contents error: " + e); }

        Log.i(TAG, "Channel " + channelId + ": title='" + c.title
            + "' subs=" + c.subscriberCount
            + " videos=" + c.videos.size()
            + " avatar=" + (!c.avatarUrl.isEmpty())
            + " banner=" + (!c.bannerUrl.isEmpty()));
        return c;
    }

    private static void walkSection(JsonObject section, ChannelData c) {
        try {
            if (section.has("itemSectionRenderer")) {
                JsonObject isr = section.getAsJsonObject("itemSectionRenderer");
                if (isr.has("contents") && isr.get("contents").isJsonArray()) {
                    JsonArray arr = isr.getAsJsonArray("contents");
                    for (int i = 0; i < arr.size(); i++) {
                        addVideo(arr.get(i).getAsJsonObject(), c);
                    }
                }
            }
            if (section.has("gridRenderer")) {
                JsonObject gr = section.getAsJsonObject("gridRenderer");
                if (gr.has("items") && gr.get("items").isJsonArray()) {
                    JsonArray arr = gr.getAsJsonArray("items");
                    for (int i = 0; i < arr.size(); i++) {
                        addVideo(arr.get(i).getAsJsonObject(), c);
                    }
                }
            }
            if (section.has("shelfRenderer")) {
                JsonObject sh = section.getAsJsonObject("shelfRenderer");
                if (sh.has("content") && sh.get("content").isJsonObject()) {
                    JsonObject sc = sh.getAsJsonObject("content");
                    if (sc.has("horizontalListRenderer")) {
                        JsonObject hl = sc.getAsJsonObject("horizontalListRenderer");
                        if (hl.has("items") && hl.get("items").isJsonArray()) {
                            JsonArray arr = hl.getAsJsonArray("items");
                            for (int i = 0; i < arr.size(); i++) {
                                addVideo(arr.get(i).getAsJsonObject(), c);
                            }
                        }
                    }
                    if (sc.has("gridRenderer")) {
                        JsonObject gr = sc.getAsJsonObject("gridRenderer");
                        if (gr.has("items") && gr.get("items").isJsonArray()) {
                            JsonArray arr = gr.getAsJsonArray("items");
                            for (int i = 0; i < arr.size(); i++) {
                                addVideo(arr.get(i).getAsJsonObject(), c);
                            }
                        }
                    }
                    if (sc.has("verticalListRenderer")) {
                        JsonObject vl = sc.getAsJsonObject("verticalListRenderer");
                        if (vl.has("items") && vl.get("items").isJsonArray()) {
                            JsonArray arr = vl.getAsJsonArray("items");
                            for (int i = 0; i < arr.size(); i++) {
                                addVideo(arr.get(i).getAsJsonObject(), c);
                            }
                        }
                    }
                }
            }
            if (section.has("richItemRenderer")) {
                addVideo(section, c);
            }
        } catch (Exception e) {}
    }

    private static void addVideo(JsonObject item, ChannelData c) {
        try {
            VideoData v = null;

            if (item.has("richItemRenderer")) {
                JsonObject ri = item.getAsJsonObject("richItemRenderer");
                if (ri.has("content") && ri.get("content").isJsonObject()) {
                    JsonObject ct = ri.getAsJsonObject("content");
                    if (ct.has("videoRenderer")) v = VideoData.fromRenderer(ct.getAsJsonObject("videoRenderer"));
                    else if (ct.has("videoWithContextRenderer")) v = VideoData.fromVideoWithContext(ct.getAsJsonObject("videoWithContextRenderer"));
                }
            } else if (item.has("videoWithContextRenderer")) {
                v = VideoData.fromVideoWithContext(item.getAsJsonObject("videoWithContextRenderer"));
            } else if (item.has("videoRenderer")) {
                v = VideoData.fromRenderer(item.getAsJsonObject("videoRenderer"));
            } else if (item.has("gridVideoRenderer")) {
                v = VideoData.fromRenderer(item.getAsJsonObject("gridVideoRenderer"));
            } else if (item.has("compactVideoRenderer")) {
                v = VideoData.fromRenderer(item.getAsJsonObject("compactVideoRenderer"));
            }

            if (v != null && !v.id.isEmpty()) {
                for (VideoData existing : c.videos) {
                    if (existing.id.equals(v.id)) return;
                }
                c.videos.add(v);
            }
        } catch (Exception e) {}
    }
}
