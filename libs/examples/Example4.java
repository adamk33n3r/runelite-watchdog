package jaco.mp3.player.examples;

import jaco.mp3.player.MP3Player;

import java.net.URL;

public class Example4 {

  public static void main(String[] args) throws Exception {

    URL url1 = new URL("http://server.com/mp3s/test1.mp3");
    URL url2 = new URL("http://server.com/mp3s/test2.mp3");
    URL url3 = new URL("http://server.com/mp3s/test3.mp3");

    new MP3Player(url1, url2, url3).play();
  }

}
