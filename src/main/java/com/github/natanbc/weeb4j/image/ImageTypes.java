package com.github.natanbc.weeb4j.image;

import com.github.natanbc.weeb4j.Weeb4J;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public final class ImageTypes {
    private final List<String> types;
    private final List<PreviewImage> previewImages;

    private ImageTypes(List<String> types, List<PreviewImage> previewImages) {
        this.types = types;
        this.previewImages = previewImages;
    }

    /**
     * Returns the image types supported.
     *
     * @return The image types.
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     * Returns preview images for the image types. These only change if the current preview is deleted.
     *
     * @return The preview images, or an empty list if preview images were disabled in the request.
     */
    public List<PreviewImage> getPreviewImages() {
        return previewImages;
    }

    /**
     * Returns a preview image for this type, or nothing if there's no image.
     *
     * @param type The type to get a preview of.
     *
     * @return An optional containing an existing preview for the type, or an empty optional.
     */
    public Optional<PreviewImage> getPreviewImageByType(String type) {
        for(PreviewImage i : previewImages) {
            if(i.getType().equalsIgnoreCase(type)) return Optional.of(i);
        }
        return Optional.empty();
    }

    @Nonnull
    @CheckReturnValue
    public static ImageTypes fromJSON(@Nonnull Weeb4J api, @Nonnull JSONObject json) {
        JSONArray typesRaw = json.getJSONArray("types");
        List<String> types = new ArrayList<>(typesRaw.length());
        for(int i = 0, j = typesRaw.length(); i < j; i++) {
            types.add(typesRaw.getString(i));
        }
        JSONArray previewsRaw = json.optJSONArray("preview");
        List<PreviewImage> previews = new ArrayList<>();
        if(previewsRaw != null) {
            for(int i = 0, j = previewsRaw.length(); i < j; i++) {
                previews.add(PreviewImage.fromJSON(api, previewsRaw.getJSONObject(i)));
            }
        }
        return new ImageTypes(
                Collections.unmodifiableList(types),
                Collections.unmodifiableList(previews)
        );
    }
}
