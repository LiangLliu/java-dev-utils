package com.LiangLliu.utils.stream.sort;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Node {
    private Integer score;
    private String name;
    private Integer ranking;

    public Node(Integer score, String name) {
        this.score = score;
        this.name = name;
    }
}
