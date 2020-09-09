package com.akxy.dynamicdb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangp
 */
public class DbQueryUtil {

    public static int[] getPage(List<Integer> pages, int queryPage) {
        int sum = pages.stream().mapToInt(Integer::intValue).sum();
        if (queryPage > sum) {
            return new int[]{-1, -1};
        }
        return getPage(new ArrayList<>(pages), arange(pages), queryPage, 0);
    }

    private static int[] getPage(List<Integer> pages, List<Integer> pageIndexes, int queryPage, int base) {
        if (pages.isEmpty()) {
            return new int[]{-1, -1};
        }
        Integer minPage = pages.stream().min(Integer::compareTo).get();
        Integer lastPageSize = pages.size();
        if (pages.size() * minPage > queryPage) {
            int index = queryPage % pages.size();
            int position = base + queryPage / pages.size();
            return new int[]{pageIndexes.get(index), position};
        } else {
            while (pages.contains(minPage)) {
                pageIndexes.remove(pages.indexOf(minPage));
                pages.remove(minPage);
            }
            int nextBase = base + minPage;
            int nextQueryPage = queryPage - minPage * lastPageSize;
            List<Integer> nextPages = pages.stream().map(i -> i - minPage).collect(Collectors.toList());
            return getPage(nextPages, pageIndexes, nextQueryPage, nextBase);
        }

    }

    private static List<Integer> arange(List<Integer> arr) {
        List<Integer> indexs = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            indexs.add(i);
        }
        return indexs;
    }
}
