package jaco.mp3.player.examples;

import jaco.mp3.player.MP3Player;

import java.io.File;

public class Example1 {

  public static void main(String[] args) {
    new MP3Player(new File("test.mp3")).play();
  }

}
