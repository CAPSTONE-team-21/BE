package org.sspoid.sspoid.db.chatsession;

public enum SkinTpye {
    DRY1(SkinType.DRY),
    DRY2(SkinType.DRY),
    DRY3(SkinType.DRY),

    COMBINED1(SkinType.COMBINED),
    COMBINED2(SkinType.COMBINED),
    COMBINED3(SkinType.COMBINED),

    OILY1(SkinType.OILY),
    OILY2(SkinType.OILY),
    OILY3(SkinType.OILY),

    SENSITIVE1(SkinType.SENSITIVE),
    SENSITIVE2(SkinType.SENSITIVE),
    SENSITIVE3(SkinType.SENSITIVE);

    private final SkinType skinGroup;

    SkinTpye(SkinType skinGroup) {
        this.skinGroup = skinGroup;
    }

}
