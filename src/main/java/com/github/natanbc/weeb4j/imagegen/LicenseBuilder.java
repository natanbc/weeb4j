package com.github.natanbc.weeb4j.imagegen;

import com.github.natanbc.weeb4j.util.Utils;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class LicenseBuilder {
    private final String title;
    private final String avatar;
    private List<String> badges;
    private List<String> widgets;

    /**
     * Creates a new builder with a given title and avatar.
     *
     * @param title Title to show on the card. May not be null.
     * @param avatar Avatar to show on the card. May not be null and must be a valid URL.
     */
    public LicenseBuilder(String title, String avatar) {
        Utils.notEmpty(title);
        Utils.validUrl(avatar);

        this.title = title;
        this.avatar = avatar;
    }

    /**
     * Set the badges to be displayed. All strings must be valid URLs.
     *
     * @param badges Badges to be displayed. Must be 3 or less.
     *
     * @return This builder for chaining calls.
     */
    @CheckReturnValue
    @Nonnull
    public LicenseBuilder badges(String... badges) {
        return badges(badges == null || badges.length == 0 ? null : Arrays.asList(badges.clone()));
    }

    /**
     * Set the badges to be displayed. All strings must be valid URLs.
     *
     * @param badges Badges to be displayed. Must be 3 or less.
     *
     * @return This builder for chaining calls.
     */
    @CheckReturnValue
    @Nonnull
    public LicenseBuilder badges(List<String> badges) {
        if(badges != null) {
            if(badges.size() > 3) {
                throw new IllegalArgumentException("Badges must contain 3 or less elements");
            }
            for(String s : badges) {
                Utils.validUrl(s);
            }
        }
        this.badges = badges;
        return this;
    }

    /**
     * Set the widgets to be displayed.
     *
     * @param widgets Widgets to be displayed. Must be 3 or less.
     *
     * @return This builder for chaining calls.
     */
    @CheckReturnValue
    @Nonnull
    public LicenseBuilder widgets(String... widgets) {
        return widgets(widgets == null || widgets.length == 0 ? null : Arrays.asList(widgets.clone()));
    }

    /**
     * Set the widgets to be displayed.
     *
     * @param widgets Widgets to be displayed. Must be 3 or less.
     *
     * @return This builder for chaining calls.
     */
    @CheckReturnValue
    @Nonnull
    public LicenseBuilder widgets(List<String> widgets) {
        if(widgets != null) {
            if(widgets.size() > 3) {
                throw new IllegalArgumentException("Widgets must contain 3 or less elements");
            }
            for(String s : widgets) {
                Utils.notEmpty(s);
            }
        }
        this.widgets = widgets;
        return this;
    }

    /**
     * Creates a new LicenseData object, used to generate a license card.
     *
     * @return The newly created LicenseData object.
     */
    public LicenseData build() {
        return new LicenseData(title, avatar, badges, widgets);
    }
}
