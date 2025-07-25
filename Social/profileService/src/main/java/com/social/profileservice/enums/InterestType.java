package com.social.profileservice.enums;

import lombok.Getter;

@Getter
public enum InterestType {
    MUSIC("Listening to or playing music", InterestCategory.ENTERTAINMENT),
    TRAVEL("Exploring new places and cultures", InterestCategory.ADVENTURE),
    SPORTS("Engaging in or watching sports", InterestCategory.HEALTH),
    READING("Reading books and articles", InterestCategory.EDUCATION),
    GAMING("Playing games", InterestCategory.ENTERTAINMENT),
    COOKING("Preparing and experimenting with food", InterestCategory.LIFESTYLE),

    PHOTOGRAPHY("Capturing moments and landscapes", InterestCategory.ART),
    DRAWING("Sketching and digital illustration", InterestCategory.ART),
    MOVIES("Watching films and series", InterestCategory.ENTERTAINMENT),
    TECHNOLOGY("Exploring new technologies and gadgets", InterestCategory.EDUCATION),

    FITNESS("Working out and maintaining physical health", InterestCategory.HEALTH),
    MEDITATION("Practicing mindfulness and meditation", InterestCategory.HEALTH),
    FASHION("Exploring fashion trends and styles", InterestCategory.LIFESTYLE),
    GARDENING("Growing plants and maintaining gardens", InterestCategory.LIFESTYLE),

    BLOGGING("Writing and sharing articles", InterestCategory.EDUCATION),
    DIY("Creating handmade or custom projects", InterestCategory.LIFESTYLE),
    VOLUNTEERING("Helping in community or charity", InterestCategory.SOCIAL),
    INVESTING("Learning and applying personal finance", InterestCategory.EDUCATION),
    LANGUAGES("Learning new languages", InterestCategory.EDUCATION);

    private final String description;
    private final InterestCategory category;

    InterestType(String description, InterestCategory category) {
        this.description = description;
        this.category = category;
    }

}
