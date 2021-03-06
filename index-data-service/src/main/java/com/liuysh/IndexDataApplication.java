package com.liuysh;

import brave.sampler.Sampler;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableEurekaClient
@EnableCaching
public class IndexDataApplication {
    public static void main(String[] args) {
        int port = 0;
        int defaultPort = 8021;
        int redisPort = 6379;

        // 如果args有端口号参数就用args的
        if(args != null && args.length != 0) {
            for (String arg : args) {
                if(arg.startsWith("port=")) {
                    String strPort = StrUtil.subAfter(arg, "port=", true);
                    if(NumberUtil.isNumber(strPort)) {
                        port = Convert.toInt(strPort);
                    }
                }

            }
        }
        // 否则就在控制台输入一个端口号
        if(0==port) {
            FutureTask<Integer> task = new FutureTask(() -> {
                int p;
                System.out.printf("请于5秒钟内输入端口号, 推荐  %d, 超过5秒将默认使用 %d %n", defaultPort, defaultPort);
                Scanner sc = new Scanner(System.in);
                while (true) {
                    String strPort = sc.nextLine();
                    if(!NumberUtil.isNumber(strPort)) {
                        System.err.println("非法，只能是数字");
                    }else {
                        p = Convert.toInt(strPort);
                        sc.close();
                        break;
                    }
                }
                return p;
            });
            Thread t = new Thread(task);
            t.start();
            try {
                port = task.get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                port = defaultPort;
            }
        }

        // 然后判断server、redis、自己 这三个端口号是否都是可用的
        int eurekaServerPort = 8761;
        if(NetUtil.isUsableLocalPort(eurekaServerPort)) {
            System.err.printf("检查到端口%d 未启用，判断 eureka 服务器没有启动，本服务无法使用，故退出%n", eurekaServerPort );
            System.exit(1);
        }
        if(NetUtil.isUsableLocalPort(redisPort)) {
            System.err.printf("检查到端口%d 未启用，判断 redis 服务器没有启动，本服务无法使用，故退出%n", redisPort );
            System.exit(1);
        }
        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }
        // 反射的方式启动自己
        new SpringApplicationBuilder(IndexDataApplication.class).properties("server.port=" + port).run(args);
    }
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
}
