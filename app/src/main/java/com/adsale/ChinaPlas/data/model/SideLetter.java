package com.adsale.ChinaPlas.data.model;

/**
 * 展商列表右侧的索引条
 * Created by Carrie on 2017/8/12.
 */

public class SideLetter {
    public String letter;

    public SideLetter(String letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        return "SideLetter{" +
                "letter='" + letter + '\'' +
                '}';
    }
}
