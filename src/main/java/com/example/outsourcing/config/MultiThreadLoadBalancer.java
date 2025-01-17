package com.example.outsourcing.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadLoadBalancer {

    public static void main(String[] args) {
        LoadBalancer loadBalancer = new LoadBalancer();
        loadBalancer.addServer("http://localhost:8081");
        loadBalancer.addServer("http://localhost:8082");
        loadBalancer.addServer("http://localhost:8083");

        //10개의 스레드가 있는 고정된 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        //비동기 요청 실행
        //10개의 요청을 스레드 풀을 통해 병렬로 실행함
        //각 요청은 로드밸런서를 통해 분배되며, 결과를 콘솔에 출력
        for(int i = 0; i < 10; i++){
            final int requestId = i + 1;
            executorService.execute(() -> {
                try{
                    String response = loadBalancer.forwardRequest("/api/example");
                    System.out.println("RequestId:" + requestId + " Response:" + response);
                }catch(Exception e){
                    e.printStackTrace();
                }
            });
        }
        //모든 요청 종료 뒤 스레드 풀 종료
        executorService.shutdown();
    }
}
