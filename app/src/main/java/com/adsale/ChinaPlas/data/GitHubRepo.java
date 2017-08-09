package com.adsale.ChinaPlas.data;

/**
 * Created by Carrie on 2017/8/9.
 */

public class GitHubRepo {
    private int id;
    private String name;

    GitHubRepo(){

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "GitHubRepo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
