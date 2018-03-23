package com.github.natanbc.weeb4j.imagegen;

import java.util.List;

public class LicenseData {
    private final String title;
    private final String avatar;
    private final List<String> badges;
    private final List<String> widgets;

    LicenseData(String title, String avatar, List<String> badges, List<String> widgets) {
        this.title = title;
        this.avatar = avatar;
        this.badges = badges;
        this.widgets = widgets;
    }

    public String getTitle() {
        return title;
    }

    public String getAvatar() {
        return avatar;
    }

    public List<String> getBadges() {
        return badges;
    }

    public List<String> getWidgets() {
        return widgets;
    }
}
