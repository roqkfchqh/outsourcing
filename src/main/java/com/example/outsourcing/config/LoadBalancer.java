package com.example.outsourcing.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancer {

    //로그밸런서가 관리하는 서버 인스턴스의 url 리스트
    private final List<String> servers;
    //라운드로빈 방식으로 순서를 추적하는 변수
    private int currentIndex = -1;

    public LoadBalancer(){
        servers = new ArrayList<>();
    }

    //서버 url 을 리스트에 추가하는 메서드
    public void addServer(String server){
        servers.add(server);
    }

    //다음 서버 선택(라운드로빈 방식)
    //현재 인덱스를 1 증가시키며, 리스트의 끝에 도달하면 다시 처음으로 되돌아옴.
    //synchronized 를 통해 멀티스레드 환경에서도 동기화 가능
    public synchronized String getNextServer(){
        if(servers.isEmpty()){
            throw new IllegalStateException("No servers available");
        }
        currentIndex = (currentIndex + 1) % servers.size();
        return servers.get(currentIndex);
    }

    //다음 서버를 선택하고, 클라이언트 요청을 해당 서버로 전달하는 메서드
    //HttpURLConnection 을 사용해 HTTP GET 요청을 보냄 -> 응답 결과를 읽고 반환
    public String forwardRequest(String clientRequest) throws Exception{
        String server = getNextServer();
        URL url = new URL(server + clientRequest);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    //로드밸런서를 초기화하고 서버를 초기화한 뒤 요청을 여러 번 보내 분배 결과를 확인함
    public static void main(String[] args){
        //로드밸런서 초기화
        LoadBalancer loadBalancer = new LoadBalancer();

        //서버 인스턴스 추가
        loadBalancer.addServer("http://localhost:8081");
        loadBalancer.addServer("http://localhost:8082");
        loadBalancer.addServer("http://localhost:8083");

        try{
            //요청 라우팅 테스트
            for(int i = 0; i < 5; i++){
                String response = loadBalancer.forwardRequest("/api/example");
                System.out.println("Response: " + response);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
