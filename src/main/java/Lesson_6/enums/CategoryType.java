package Lesson_6.enums;

import lombok.Getter;

public enum CategoryType {
    FOOD(1, "Food"),
    ELECTRONICS(2, "Electronics");

    @Getter
    public final long id;
    @Getter
    public final String title;

    CategoryType(long id, String title) {
        this.id = id;
        this.title = title;
    }
}
