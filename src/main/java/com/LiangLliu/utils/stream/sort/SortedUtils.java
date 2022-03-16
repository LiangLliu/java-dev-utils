package com.LiangLliu.utils.stream.sort;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

public class SortedUtils {
    /**
     * @param stream             需要排序的list
     * @param propertyExtractor  ： 排序的字段
     * @param propertyComparator ： 顺序
     * @param <T>                : 排序列表的元素类型
     * @param <V>                ：
     * @return ：SortedMap<Integer, List<T>>
     */
    public static <T, V> SortedMap<Integer, List<T>> ranking(Stream<T> stream,
                                                             Function<T, V> propertyExtractor,
                                                             Comparator<V> propertyComparator) {
        return stream.sorted(comparing(propertyExtractor, propertyComparator))
                .collect(TreeMap::new,
                        (rank, item) -> {
                            V property = propertyExtractor.apply(item);
                            if (rank.isEmpty()) {
                                rank.put(1, new LinkedList<>());
                            } else {
                                Integer r = rank.lastKey();
                                List<T> items = rank.get(r);
                                if (!property.equals(propertyExtractor.apply(items.get(0)))) {
                                    rank.put(r + items.size(), new LinkedList<>());
                                }
                            }
                            rank.get(rank.lastKey()).add(item);
                        },
                        (rank1, rank2) -> {
                            int lastRanking = rank1.lastKey();
                            int offset = lastRanking + rank1.get(lastRanking).size() - 1;
                            if (propertyExtractor.apply(rank1.get(lastRanking).get(0))
                                    == propertyExtractor.apply(rank2.get(rank2.firstKey()).get(0))) {
                                rank1.get(lastRanking).addAll(rank2.get(rank2.firstKey()));
                                rank2.remove(rank2.firstKey());
                            }
                            rank2.forEach((r, items) -> rank1.put(offset + r, items));
                        }
                );
    }

    /**
     * 设置对象中的排名属性
     *
     * @param sortedMap
     * @param biConsumer
     * @param <T>        : 排名的对象
     * @param <V>        ：名词属性：eg ranking字段
     * @return ：设置完排名后的list
     */
    public static <T, V> List<T> convert(SortedMap<V, List<T>> sortedMap, BiConsumer<T, V> biConsumer) {

        sortedMap.forEach((key, value) ->
                value = value.stream()
                        .peek(it -> biConsumer.accept(it, key))
                        .collect(Collectors.toList()));

        return flatMap(sortedMap.values());
    }

    private static <T> List<T> flatMap(Collection<List<T>> lists) {
        return lists.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
