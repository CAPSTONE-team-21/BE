package org.sspoid.sspoid.db.chatsession;

public enum SkinTpye {
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

    SkinTpye(SkinGroup skinGroup) {
        this.skinGroup = skinGroup;
    }

}
