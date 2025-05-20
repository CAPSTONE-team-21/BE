package org.sspoid.sspoid.db.chatsession;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SkinType {
    DRY1(SkinGroup.DRY),
    DRY2(SkinGroup.DRY),
    DRY3(SkinGroup.DRY),

    COMBINED1(SkinGroup.COMBINED),
    COMBINED2(SkinGroup.COMBINED),
    COMBINED3(SkinGroup.COMBINED),

    OILY1(SkinGroup.OILY),
    OILY2(SkinGroup.OILY),
    OILY3(SkinGroup.OILY),

    SENSITIVE1(SkinGroup.SENSITIVE),
    SENSITIVE2(SkinGroup.SENSITIVE),
    SENSITIVE3(SkinGroup.SENSITIVE);

    private final SkinGroup skinGroup;
    SkinType(SkinGroup skinGroup) {
        this.skinGroup = skinGroup;
    }

    public SkinGroup getSkinGroup() {
        return skinGroup;
    }

    public static List<SkinType> fromSkinGroup(SkinGroup skinGroup) {
        return Arrays.stream(values())
                .filter(sub -> sub.getSkinGroup() == skinGroup)
                .collect(Collectors.toList());
    }

}
