package demo.example1.service;

import demo.example1.repository.TestRepository;
import framework.annotations.injection.semantic.Service;

import java.util.stream.Collectors;

@Service
public class CapsService {
    private final TestRepository testRepository;

    public CapsService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public String capitalizeData() {
        return testRepository.getData().stream().map(String::toUpperCase).collect(Collectors.joining(", "));
    }
}
