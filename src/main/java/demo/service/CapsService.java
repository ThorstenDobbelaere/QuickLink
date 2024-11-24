package demo.service;

import demo.repository.TestRepository;
import framework.annotations.injection.semantic.Service;
import framework.annotations.interception.Timed;

import java.util.stream.Collectors;

@Service
public class CapsService {
    private final TestRepository testRepository;

    public CapsService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Timed
    public String capitalizeData() {
        return testRepository.getData().stream().map(String::toUpperCase).collect(Collectors.joining(", "));
    }
}
