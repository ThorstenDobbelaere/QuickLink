package demo.example1.repository;

import framework.annotations.injection.semantic.Repository;

import java.util.List;

@Repository
public class TestRepository {
    private static final List<String> data = List.of("Apple", "Banana", "Lemon", "Orange");

    public List<String> getData() {
        return data;
    }
}
