package jaco.mp3.player.examples;

import jaco.mp3.player.MP3Player;

import java.io.File;

public class Example6 {

  public static void main(String[] args) throws Exception {

    MP3Player player = new MP3Player();

    player.addToPlayList(new File("test1.mp3"));
    player.addToPlayList(new File("test2.mp3"));
    player.addToPlayList(new File("test3.mp3"));

    player.setRepeat(true);
    player.setShuffle(true);

    player.play();
  }

}
