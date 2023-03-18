package randomizedtest;


import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import javax.security.auth.login.AccountException;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> AList = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();
        AList.addLast(4);
        buggyAList.addLast(4);
        AList.addLast(5);
        buggyAList.addLast(5);
        AList.addLast(6);
        buggyAList.addLast(6);
        while (AList.size() != 0 && buggyAList.size() != 0) {
            if (AList.removeLast().equals(buggyAList.removeLast())) {
                continue;
            }
            System.out.println("失败");
        }
        System.out.println("成功");
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> AList = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();
        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                AList.addLast(randVal);
                buggyAList.addLast(randVal);
                //System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                if(AList.size()==buggyAList.size()){
                    continue;
                }
                System.out.println("测试失败,AList长度为"+AList.size()+",buggyAList长度为"+buggyAList.size());
                // size
                //int size = L.size();
                //System.out.println("size: " + size);
            }
            if(AList.size()==0 && buggyAList.size()==0){
                continue;
            }
            if (operationNumber == 2) {
                //int randVal = StdRandom.uniform(0, 100);
                int num1 = AList.removeLast();
                int num2 = buggyAList.removeLast();
                if(num1==num2){
                    continue;
                }
                System.out.println("测试失败,AList最后一个元素为"+num1+",buggyAList最后一个元素为"+num2);
            }else if(operationNumber == 3){
                if(AList.getLast().equals(buggyAList.getLast())){
                    continue;
                }
                System.out.println("测试失败,AList最后一个元素为"+AList.getLast()+",buggyAList最后一个元素为"+buggyAList.getLast());
            }
        }
    }

}