package demo.service;

import demo.repository.TestRepository;
import framework.annotations.Injectable;

import java.util.List;

@Injectable
public class TestService {
    private final TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public String concatData(){
        List<String> data = testRepository.getData();
        return String.join(", ", data);
    }

    public int count(){
        return testRepository.incrementCounter();
    }
}
