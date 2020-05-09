package com.example.demo.netty.jdk_stream;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: hutaisi@worken.cn
 * @create: 2019-11-18 11:15
 **/
public class StreamTest {

    public static void main(String[] args) {

        StreamTest streamTest = new StreamTest();

//        streamTest.testList();

        streamTest.testListMap();

    }

    public void testList(){

        List<String> stringList = Arrays.asList("a", "b", "c", "", "e", "f");

        List<String> collect = stringList.stream().filter(string -> !string.isEmpty()).collect(
                Collectors.toList()
        );
        collect.stream().forEach(System.out :: println);
    }


    public void testListMap(){

        List<Integer> intList = Arrays.asList(3,2,3,4,5);
        List<Integer> collect = intList.stream().map(i -> i * i).distinct().collect(Collectors.toList());

        collect.forEach(System.out :: println);
    }





}
