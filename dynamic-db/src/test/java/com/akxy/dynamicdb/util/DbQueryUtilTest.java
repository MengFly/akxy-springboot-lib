package com.akxy.dynamicdb.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class DbQueryUtilTest {


    @Test
    public void testGetPage() {
        testGetPage(Arrays.asList(10, 20, 30, 40, 20, 10, 30, 40));
        testGetPage(Arrays.asList(40, 20, 10, 30, 20, 10, 30, 40));
        testGetPage(Arrays.asList(20, 40, 10, 30, 20, 10, 30, 40));
    }


    public void testGetPage(List<Integer> pages) {
        if (pages.isEmpty()) {
            return;
        }
        int max = pages.stream().max(Integer::compareTo).get();
        int sum = pages.stream().mapToInt(Integer::intValue).sum();
        int[][] data = new int[pages.size()][max];
        for (int i = 0; i < sum; i++) {
            int[] page = DbQueryUtil.getPage(pages, i);
            if (page[0] != -1) {
                data[page[0]][page[1]] = i;
            }
        }
        System.out.println("======================");
        System.out.print("\\ \t");
        for (int i = 0; i < pages.size(); i++) {
            System.out.print(i + "\t");
        }
        System.out.println();
        for (int i = 0; i < max; i++) {
            System.out.print(i + "\t");
            for (int j = 0; j < pages.size(); j++) {
                int i1 = data[j][i];
                if (i1 == 0) {
                    System.out.print(" " + "\t");
                } else {
                    System.out.print(i1 + "\t");
                }
            }
            System.out.println();
        }
        System.out.println("======================");
    }
}
