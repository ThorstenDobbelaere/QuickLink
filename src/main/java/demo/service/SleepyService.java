package demo.service;

import framework.annotations.injection.semantic.Service;
import framework.annotations.interception.Timed;

import java.util.Random;

@Service
public class SleepyService {
    public void bigTask() {
        subTask1();
        subTask2();
        subTask3();
    }

    @Timed
    public void subTask1(){
        sleep(100, 200);
    }

    @Timed
    protected void subTask2(){
        sleep(50, 300);
    }

    @Timed
    void subTask3(){
        sleep(100, 300);
    }

    private void sleep(int min, int max){
        Random random = new Random();
        int ms = random.nextInt(max - min) + min;
        try{
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
