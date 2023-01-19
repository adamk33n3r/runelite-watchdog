package jaco.mp3.player.examples;

import jaco.mp3.player.MP3Player;

import java.io.File;

public class Example3 {

  public static void main(String[] args) throws Exception {

    File file1 = new File("test1.mp3");
    File file2 = new File("test2.mp3");
    File file3 = new File("test3.mp3");

    new MP3Player(file1, file2, file3).play();
  }

}
