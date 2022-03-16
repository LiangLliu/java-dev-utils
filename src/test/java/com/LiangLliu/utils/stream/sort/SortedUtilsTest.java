package com.LiangLliu.utils.stream.sort;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.reverseOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SortedUtilsTest {
    @Test
    public void should_get_ranking() {
        List<Node> nodes = Stream.iterate(1, n -> n + 1)
                .limit(1000000)
                .map(it -> new Node(it, it + ""))
                .collect(Collectors.toList());

        SortedMap<Integer, List<Node>> rank = SortedUtils.ranking(nodes.stream(), Node::getScore, reverseOrder());

        List<Node> extracted = SortedUtils.convert(rank, Node::setRanking);
        Node node = extracted.get(0);
        assertEquals(1000000, node.getScore());
        assertEquals("1000000", node.getName());
        assertEquals(1, node.getRanking());
    }

}
