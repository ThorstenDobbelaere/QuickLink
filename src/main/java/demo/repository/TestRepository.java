package demo.repository;

import framework.annotations.injection.semantic.Repository;

import java.util.List;

@Repository
public class TestRepository {
    private static final List<String> data = List.of("Apple", "Banana", "Lemon", "Orange");
    private static int counter = 0;

    public List<String> getData() {
        return data;
    }

    public int incrementCounter(){
        return counter++;
    }
}
