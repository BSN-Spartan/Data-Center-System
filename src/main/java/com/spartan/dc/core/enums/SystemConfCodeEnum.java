package com.spartan.dc.core.enums;

/**
 * @author wxq
 * @create 2022/8/10 19:27
 * @description chain type
 */
public enum SystemConfCodeEnum {
    TITLE("title", "title"),
    LOGO("logo", "logo"),
    HEADLINE("headline", "headline"),
    INTRODUCE("introduce", "introduce"),
    COPYRIGHT("copyright", "copyright"),
    SERVICETERMS("ServiceTerms","ServiceTerms"),
    TPS("tps", "tps"),
    TPD("tpd", "tpd"),

    TREATY("treaty", "treaty"),

    TELEGRAM("Telegram", "Telegram"),
    WHATSAPP("Whatsapp", "Whatsapp"),
    REDDIT("Reddit", "Reddit"),
    DISCORD("Discord", "Discord"),
    TWITTER("Twitter", "Twitter"),

    ADDRESS("address", "address"),
    EMAIL("email", "email"),
    PHONE("phone", "phone"),
    NO_MORE_PROMPTS_INPUT("noMorePromptsInput", "NoMorePromptsInput");

    private final String code;
    private final String name;

    SystemConfCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
