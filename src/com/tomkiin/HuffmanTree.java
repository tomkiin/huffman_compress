package com.tomkiin;

import java.util.*;

class HuffmanTree {
    HuffmanNode root;
    List<HuffmanNode> initHuffmanNode(Map<Character, Integer> wordCount) {
        List<HuffmanNode> nodes = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : wordCount.entrySet()) {
            nodes.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }
        return nodes;
    }

    private void sortValue(List<HuffmanNode> nodes) {  // 对节点链表进行升序排序
        nodes.sort(new Comparator<HuffmanNode>() {
            @Override
            public int compare(HuffmanNode o1, HuffmanNode o2) {
                return o1.weight - o2.weight;
            }
        });
    }

    void createHuffmanTree(List<HuffmanNode> nodes) {  // 构建哈夫曼树，将每轮权值最小的两个节点构成一个树
        while (nodes.size() > 1) {
            sortValue(nodes);  // 对节点列表进行升序排列
            HuffmanNode left = nodes.get(0);
            HuffmanNode right = nodes.get(1);
            HuffmanNode parent = new HuffmanNode('*', (left.weight + right.weight));
            parent.lChild = left;
            parent.rChild = right;
            nodes.remove(left);
            nodes.remove(right);
            nodes.add(parent);
        }
        root = nodes.get(0);
        System.out.println("根节点为: " + root);
    }

}
