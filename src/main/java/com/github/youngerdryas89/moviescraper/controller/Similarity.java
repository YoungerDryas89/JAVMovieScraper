package com.github.youngerdryas89.moviescraper.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Similarity {
    public static int calculateLevenshteinDistance(String str1, String str2){
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
        for(int i = 0; i <= str1.length(); i++){
            for(int j = 0; j <= str2.length(); j++){
                if(i == 0)
                    dp[i][j] = j;
                else if(j == 0)
                    dp[i][j] = i;
                else {
                    dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(str1.charAt(i - 1), str2.charAt(j  - 1)),
                            dp[i - 1][j] + 1, dp[i][j - 1] + 1);
                }
            }
        }
        return dp[str1.length()][str2.length()];
    }

    public static int calculateNormalizedLevenshteinDistance(String str1, String str2){
        var score = calculateLevenshteinDistance(str1, str2);
        return 1 - (score / Math.max(str1.length(), str2.length()));
    }

    public static double calculateJaccardIndex(String str1, String str2){
        var s1 = new HashSet<String>(List.of(str1.split(" ")));
        var s2 = new HashSet<String>(List.of(str2.split(" ")));

        var union = Stream.concat(s1.stream(), s1.stream()).collect(Collectors.toSet());
        var intersection = s1.stream().filter(s2::contains).collect(Collectors.toSet());


        return (double) intersection.size() / union.size();

    }
    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }

}
