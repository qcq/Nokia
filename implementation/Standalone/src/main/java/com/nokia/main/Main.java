package com.nokia.main;

import com.nokia.certificate.Keys;
import com.nokia.config.Config;
import com.nokia.config.IConfig;
import com.nokia.java8.Stream;
import com.nokia.keystore.KeyStoreService;
import com.nokia.pats.MostOccurLetters;
import com.nokia.pats.basic.PalindromicNumber1079;
import com.nokia.pats.interviews.Combination;
import com.nokia.pats.interviews.PartialCombination;
import com.nokia.pats.interviews.PrimeNumber;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.security.rsa.RSAPublicKeyImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;


/**
 * Created by chuanqin on 2017/6/19.
 */
public class Main {
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);
  private static final String LOG_CONFIG_FILE_PATH =
        "/Users/qinchuanqing/Code/workspace/Nokia/implementation/Standalone/src/main/resources/log4j.propertiess";
  private static final String SPRING_CONFIG_FILE_PATH = "classpath*:spring.xml";
  private static final long LOG_CONFIG_CHECK_DELAY_MS = 10;

  private static final String file_name_cert4 = "D:/Downloads/IPsecAllL4.pem";

  private ApplicationContext context;
  private Random random;
  private int count;

  public Main() {
    initial();
  }

  public Main(int a) {
    count = a;
  }


  private void initial() {
    PropertyConfigurator.configureAndWatch(LOG_CONFIG_FILE_PATH, LOG_CONFIG_CHECK_DELAY_MS);
    context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILE_PATH);
  }

  public ApplicationContext getContext() {
    return context;
  }

  public UUID generateId() {
    this.random = new Random();
    byte[] randomBytes = new byte[16];
    this.random.nextBytes(randomBytes);
    System.out.println(randomBytes);

    long mostSigBits = 0;
    for (int i = 0; i < 8; i++) {
      mostSigBits = (mostSigBits << 8) | (randomBytes[i] & 0xff);
      System.out.println(mostSigBits);
    }

    long leastSigBits = 0;
    for (int i = 8; i < 16; i++) {
      leastSigBits = (leastSigBits << 8) | (randomBytes[i] & 0xff);
      System.out.println(leastSigBits);
    }

    return new UUID(mostSigBits, leastSigBits);
  }

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    String number = in.next();
    Main test = new Main();
    IConfig config = test.getContext().getBean(Config.class);
    //System.out.println(config.toProperties());
    KeyStoreService keyStoreService = test.getContext().getBean(KeyStoreService.class);
    Keys keys = test.getContext().getBean(Keys.class);
    System.out.println(((RSAPublicKeyImpl)(keys.generateNewKeyPair().getPublic())).getModulus().bitLength());
    Main testAgain = new Main(5);
    System.out.println(testAgain.count);

    Combination.listAll(Arrays.asList("1", "2", "3"), "");
    MostOccurLetters.countTheMostOccurLetters("sfbfsakduhsadasld");
    PalindromicNumber1079.changeToPalindromicNumber(number, 10);

    PartialCombination.listAll(number, "");
    List<Integer> prime1 = PrimeNumber.getPrimeNumberByScreeningMethod(100);
    prime1.stream().forEach(item -> System.out.print(item + " "));
    System.out.println();
    List<Integer> prime2 = PrimeNumber.getPrimeNumberBySqrt(100);
    prime2.stream().forEach(item -> System.out.print(item + " "));
    Stream  streamTest = new Stream();
    streamTest.testSequentialSort();
    streamTest.testParallelSort();
  }
}
