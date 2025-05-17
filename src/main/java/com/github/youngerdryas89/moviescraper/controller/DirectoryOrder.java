package com.github.youngerdryas89.moviescraper.controller;

public enum DirectoryOrder {
    Ascending(3),
    Descending(4);

    final int value;

    DirectoryOrder(int i ){
        this.value = i;
    }

    public String toString(){
        return Integer.toString(value);
    }

    public String symbolToString(){
        return switch (value) {
            case 3 -> "Ascending";
            case 4 -> "Descending";
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    public int getValue(){
        return value;
    }
}
