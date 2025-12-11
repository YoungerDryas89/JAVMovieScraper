package com.github.youngerdryas89.moviescraper.controller;

public enum DirectorySort {
    Alphabetically(0),
    DateModified(1),
    Size(2);

    final int value;

    DirectorySort(int i) {
        this.value = i;
    }

    public String toString(){
        return Integer.toString(value);
    }

    public String symbolToString(){
        return switch (value) {
            default -> throw new IllegalStateException("Unexpected value: " + value);
            case 0 -> "Title";
            case 1 -> "Date Modified";
            case 2 -> "Size";
        };
    }

    public int getValue(){
        return value;
    }
}
