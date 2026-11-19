package com.ytlegacy.proxy;

public class GDataXmlBuilder {

    private static final String BASE = "http://127.0.0.1:8080";

    public static String buildPlayerEntry(VideoData v) {
        if (v == null || v.id == null || v.id.isEmpty()) return emptyEntry();

        StringBuilder tagsB = new StringBuilder();
        for (String t : v.tags) {
            if (tagsB.length() > 0) tagsB.append(", ");
            tagsB.append(esc(t));
        }
        String keywords = tagsB.length() > 0 ? tagsB.toString() : "video";

        String hdTag = v.isHd ? "<yt:hd/>" : "";
        String liveTag = v.isLive ? "<yt:live/>" : "";
        String rating = "nonadult";

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<entry xmlns=\"http://www.w3.org/2005/Atom\" " +
            "xmlns:media=\"http://search.yahoo.com/mrss/\" " +
            "xmlns:yt=\"http://gdata.youtube.com/schemas/2007\" " +
            "xmlns:gd=\"http://schemas.google.com/g/2005\">\n" +
            "  <id>tag:youtube.com,2008:video:" + v.id + "</id>\n" +
            "  <published>" + v.published + "</published>\n" +
            "  <updated>" + v.published + "</updated>\n" +
            "  <title>" + esc(v.title) + "</title>\n" +
            "  <summary>" + esc(truncate(v.description, 500)) + "</summary>\n" +
            "  <content type=\"application/x-shockwave-flash\" src=\"" + BASE + "/v/" + v.id + "\"/>\n" +
            "  <link rel=\"self\" type=\"application/atom+xml\" href=\"" + BASE + "/feeds/api/videos/" + v.id + "?v=2\"/>\n" +
            "  <link rel=\"alternate\" type=\"text/html\" href=\"" + BASE + "/watch?v=" + v.id + "\"/>\n" +
            "  <link rel=\"http://gdata.youtube.com/schemas/2007#video.related\" type=\"application/atom+xml\" href=\"" + BASE + "/feeds/api/videos/" + v.id + "/related\"/>\n" +
            "  <link rel=\"http://gdata.youtube.com/schemas/2007#video.ratings\" type=\"application/atom+xml\" href=\"" + BASE + "/feeds/api/videos/" + v.id + "/ratings\"/>\n" +
            "  <author><name>" + esc(v.author) + "</name><uri>" + BASE + "/feeds/api/users/" + esc((v.channelId == null || v.channelId.isEmpty()) ? "unknown" : v.channelId) + "</uri></author>\n" +
            "  <yt:username>" + esc(v.channelId) + "</yt:username>\n" +
            "  <yt:userId>" + esc(v.channelId) + "</yt:userId>\n" +
            "  <yt:accessControl action=\"comment\" permission=\"allowed\"/>\n" +
            "  <yt:accessControl action=\"embed\" permission=\"allowed\"/>\n" +
            "  <yt:accessControl action=\"rate\" permission=\"allowed\"/>\n" +
            "  <yt:accessControl action=\"list\" permission=\"allowed\"/>\n" +
            "  <yt:availability status=\"public\"/>\n" +
            "  <yt:statistics favoriteCount=\"" + v.likeCount + "\" viewCount=\"" + v.viewCount + "\"/>\n" +
            "  <yt:rating numLikes=\"" + v.likeCount + "\" numDislikes=\"0\" average=\"5.0\" max=\"5\" min=\"1\"/>\n" +
            "  <gd:rating average=\"5.0\" max=\"5\" min=\"1\" numRaters=\"" + v.likeCount + "\"/>\n" +
            "  <gd:comments><gd:feedLink rel=\"http://gdata.youtube.com/schemas/2007#comments\" href=\"" + BASE + "/feeds/api/videos/" + v.id + "/comments\" countHint=\"0\"/></gd:comments>\n" +
            "  " + hdTag + liveTag + "\n" +
            "  <media:group>\n" +
            "    <media:title type=\"plain\">" + esc(v.title) + "</media:title>\n" +
            "    <media:description type=\"plain\">" + esc(v.description) + "</media:description>\n" +
            "    <media:keywords>" + keywords + "</media:keywords>\n" +
            "    <media:category label=\"" + esc(v.category) + "\" scheme=\"http://gdata.youtube.com/schemas/2007/categories.cat\">" + esc(v.category) + "</media:category>\n" +
            "    <media:content url=\"" + BASE + "/video/" + v.id + ".mp4\" type=\"video/mp4\" medium=\"video\" isDefault=\"true\" expression=\"full\" duration=\"" + v.lengthSeconds + "\" yt:format=\"18\" itag=\"18\"/>\n" +
            "    <media:thumbnail url=\"" + BASE + "/vi/" + v.id + "/3.jpg\" height=\"720\" width=\"1280\" time=\"00:00:00.000\" yt:name=\"maxresdefault\"/>\n" +
            "    <media:thumbnail url=\"" + BASE + "/vi/" + v.id + "/0.jpg\" height=\"360\" width=\"480\" time=\"00:00:00.000\" yt:name=\"hqdefault\"/>\n" +
            "    <media:thumbnail url=\"" + BASE + "/vi/" + v.id + "/2.jpg\" height=\"180\" width=\"320\" time=\"00:00:00.000\" yt:name=\"mqdefault\"/>\n" +
            "    <media:player url=\"" + BASE + "/watch?v=" + v.id + "\"/>\n" +
            "    <media:credit role=\"uploader\" scheme=\"urn:youtube\" yt:displayName=\"" + esc(v.author) + "\">" + esc(v.author) + "</media:credit>\n" +
            "    <media:rating scheme=\"urn:simple\">" + rating + "</media:rating>\n" +
            "    <media:restriction relationship=\"allow\" type=\"country\">RU US DE GB</media:restriction>\n" +
            "    <media:community>\n" +
            "      <media:starRating average=\"5.0\" count=\"" + v.likeCount + "\" min=\"1\" max=\"5\"/>\n" +
            "      <media:statistics views=\"" + v.viewCount + "\" favorites=\"" + v.likeCount + "\"/>\n" +
            "    </media:community>\n" +
            "    <yt:synopsis>" + esc(truncate(v.description, 200)) + "</yt:synopsis>\n" +
            "    <yt:duration seconds=\"" + v.lengthSeconds + "\"/>\n" +
            "    <yt:uploaded>" + v.uploadDate + "</yt:uploaded>\n" +
            "    <yt:videoid>" + v.id + "</yt:videoid>\n" +
            "  </media:group>\n" +
            "</entry>";
    }

    private static String emptyEntry() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:yt=\"http://gdata.youtube.com/schemas/2007\">\n" +
            "  <title>Not found</title>\n" +
            "</entry>";
    }

    private static String truncate(String s, int n) {
        if (s == null) return "";
        return s.length() > n ? s.substring(0, n) : s;
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }

    public static String buildChannelEntry(ChannelData c) {
        String esc_title = esc(c.title);
        String esc_desc = esc(c.description);
        String esc_id = esc(c.id);
        String avatarUrl = BASE + "/channel_avatar/" + esc_id;
        String bannerTag = (c.bannerUrl != null && !c.bannerUrl.isEmpty())
            ? "<media:banner url=\"" + BASE + "/channel_banner/" + esc_id + "\"/>"
            : "";
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<entry xmlns=\"http://www.w3.org/2005/Atom\" " +
            "xmlns:media=\"http://search.yahoo.com/mrss/\" " +
            "xmlns:yt=\"http://gdata.youtube.com/schemas/2007\" " +
            "xmlns:gd=\"http://schemas.google.com/g/2005\">\n" +
            "  <id>tag:youtube.com,2008:user:" + esc_id + "</id>\n" +
            "  <published>2013-01-01T00:00:00.000Z</published>\n" +
            "  <updated>2013-01-01T00:00:00.000Z</updated>\n" +
            "  <title>" + esc_title + "</title>\n" +
            "  <summary>" + esc_desc + "</summary>\n" +
            "  <content type=\"text\">" + esc_desc + "</content>\n" +
            "  <author><name>" + esc_title + "</name><uri>" + BASE + "/feeds/api/users/" + esc_id + "</uri></author>\n" +
            "  <link rel=\"self\" type=\"application/atom+xml\" href=\"" + BASE + "/feeds/api/channels/" + esc_id + "\"/>\n" +
            "  <yt:username>" + esc_id + "</yt:username>\n" +
            "  <yt:channelId>" + esc_id + "</yt:channelId>\n" +
            "  <yt:userId>" + esc_id + "</yt:userId>\n" +
            "  <yt:channelStatistics subscriberCount=\"" + c.subscriberCount + "\" videoCount=\"" + c.videoCount + "\" viewCount=\"0\"/>\n" +
            "  <media:thumbnail url=\"" + avatarUrl + "\" height=\"90\" width=\"90\"/>\n" +
            "  " + bannerTag + "\n" +
            "  <media:description type=\"plain\">" + esc_desc + "</media:description>\n" +
            "  <gd:feedLink rel=\"http://gdata.youtube.com/schemas/2007#user.uploads\" href=\"" + BASE + "/feeds/api/users/" + esc_id + "/uploads\" countHint=\"" + c.videoCount + "\"/>\n" +
            "  <yt:statistics subscriberCount=\"" + c.subscriberCount + "\" videoWatchCount=\"0\"/>\n" +
            "</entry>";
    }

    public static String buildChannelUploadsFeed(ChannelData c) {
        StringBuilder entries = new StringBuilder();
        int count = 0;
        for (VideoData v : c.videos) {
            String entry = buildVideoEntryXml(v);
            if (entry != null) { entries.append(entry); count++; }
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            "xmlns:media=\"http://search.yahoo.com/mrss/\" " +
            "xmlns:yt=\"http://gdata.youtube.com/schemas/2007\" " +
            "xmlns:openSearch=\"http://a9.com/-/spec/opensearch/1.1/\" " +
            "xmlns:gd=\"http://schemas.google.com/g/2005\">\n" +
            "  <id>tag:youtube.com,2008:feed:uploads:" + esc(c.id) + "</id>\n" +
            "  <title>Uploads from " + esc(c.title) + "</title>\n" +
            "  <openSearch:totalResults>" + count + "</openSearch:totalResults>\n" +
            "  <openSearch:startIndex>1</openSearch:startIndex>\n" +
            "  <openSearch:itemsPerPage>" + count + "</openSearch:itemsPerPage>\n" +
            entries.toString() +
            "</feed>";
    }

    public static String buildVideoEntryXml(VideoData v) {
        if (v == null || v.id.isEmpty()) return null;
        String keywords = "video";
        if (!v.tags.isEmpty()) {
            StringBuilder kw = new StringBuilder();
            for (String t : v.tags) {
                if (kw.length() > 0) kw.append(", ");
                kw.append(esc(t));
            }
            keywords = kw.toString();
        }
        String hdTag = v.isHd ? "<yt:hd/>" : "";
        String liveTag = v.isLive ? "<yt:live/>" : "";
        String descX = esc(truncate(v.description, 500));
        String titleX = esc(v.title);

        return "  <entry>\n" +
            "    <id>tag:youtube.com,2008:video:" + v.id + "</id>\n" +
            "    <published>" + v.published + "</published>\n" +
            "    <updated>" + v.published + "</updated>\n" +
            "    <title>" + titleX + "</title>\n" +
            "    <summary>" + descX + "</summary>\n" +
            "    <link rel=\"self\" type=\"application/atom+xml\" href=\"" + BASE + "/feeds/api/videos/" + v.id + "?v=2\"/>\n" +
            "    <link rel=\"alternate\" type=\"text/html\" href=\"" + BASE + "/watch?v=" + v.id + "\"/>\n" +
            "    <link rel=\"http://gdata.youtube.com/schemas/2007#video.related\" type=\"application/atom+xml\" href=\"" + BASE + "/feeds/api/videos/" + v.id + "/related\"/>\n" +
            "    <link rel=\"http://gdata.youtube.com/schemas/2007#video.ratings\" type=\"application/atom+xml\" href=\"" + BASE + "/feeds/api/videos/" + v.id + "/ratings\"/>\n" +
            "    <author><name>" + esc(v.author) + "</name><uri>" + BASE + "/feeds/api/users/" + esc(v.channelId) + "</uri></author>\n" +
            "    <yt:accessControl action=\"comment\" permission=\"allowed\"/>\n" +
            "    <yt:accessControl action=\"embed\" permission=\"allowed\"/>\n" +
            "    <yt:availability status=\"public\"/>\n" +
            "    <yt:statistics favoriteCount=\"" + v.likeCount + "\" viewCount=\"" + v.viewCount + "\"/>\n" +
            "    <yt:rating numLikes=\"" + v.likeCount + "\" numDislikes=\"0\" average=\"5.0\" max=\"5\" min=\"1\"/>\n" +
            "    <gd:rating average=\"5.0\" max=\"5\" min=\"1\" numRaters=\"" + v.likeCount + "\"/>\n" +
            "    <gd:comments><gd:feedLink rel=\"http://gdata.youtube.com/schemas/2007#comments\" href=\"" + BASE + "/feeds/api/videos/" + v.id + "/comments\"/></gd:comments>\n" +
            "    " + hdTag + liveTag + "\n" +
            "    <media:group>\n" +
            "      <media:title type=\"plain\">" + titleX + "</media:title>\n" +
            "      <media:description type=\"plain\">" + descX + "</media:description>\n" +
            "      <media:keywords>" + keywords + "</media:keywords>\n" +
            "      <media:category label=\"Music\" scheme=\"http://gdata.youtube.com/schemas/2007/categories.cat\">Music</media:category>\n" +
            "      <media:content url=\"" + BASE + "/video/" + v.id + ".mp4\" type=\"video/mp4\" medium=\"video\" isDefault=\"true\" expression=\"full\" duration=\"" + v.lengthSeconds + "\" yt:format=\"18\" itag=\"18\"/>\n" +
            "      <media:thumbnail url=\"" + BASE + "/vi/" + v.id + "/3.jpg\" height=\"720\" width=\"1280\" time=\"00:00:00.000\" yt:name=\"maxresdefault\"/>\n" +
            "      <media:thumbnail url=\"" + BASE + "/vi/" + v.id + "/0.jpg\" height=\"360\" width=\"480\" time=\"00:00:00.000\" yt:name=\"hqdefault\"/>\n" +
            "      <media:thumbnail url=\"" + BASE + "/vi/" + v.id + "/2.jpg\" height=\"180\" width=\"320\" time=\"00:00:00.000\" yt:name=\"mqdefault\"/>\n" +
            "      <media:player url=\"" + BASE + "/watch?v=" + v.id + "\"/>\n" +
            "      <media:credit role=\"uploader\" scheme=\"urn:youtube\" yt:displayName=\"" + esc(v.author) + "\">" + esc(v.author) + "</media:credit>\n" +
            "      <media:rating scheme=\"urn:simple\">nonadult</media:rating>\n" +
            "      <media:restriction relationship=\"allow\" type=\"country\">RU US DE GB</media:restriction>\n" +
            "      <media:community>\n" +
            "        <media:starRating average=\"5.0\" count=\"" + v.likeCount + "\" min=\"1\" max=\"5\"/>\n" +
            "        <media:statistics views=\"" + v.viewCount + "\" favorites=\"" + v.likeCount + "\"/>\n" +
            "      </media:community>\n" +
            "      <yt:duration seconds=\"" + v.lengthSeconds + "\"/>\n" +
            "      <yt:uploaded>" + v.uploadDate + "</yt:uploaded>\n" +
            "      <yt:videoid>" + v.id + "</yt:videoid>\n" +
            "    </media:group>\n" +
            "  </entry>\n";
    }
}
