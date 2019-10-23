package com.tomkiin;

public class HuffmanNode {
    char data;
    int weight;
    HuffmanNode lChild;
    HuffmanNode rChild;

    HuffmanNode(char data, int weight) {
        super();
        this.data = data;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "<" + data + "," + weight + ">";
    }
}
