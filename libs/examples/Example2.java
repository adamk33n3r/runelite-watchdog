package jaco.mp3.player.examples;

import jaco.mp3.player.MP3Player;

import java.net.URL;

public class Example2 {

  public static void main(String[] args) throws Exception {
    new MP3Player(new URL("http://server.com/mp3s/test.mp3")).play();
  }

}
