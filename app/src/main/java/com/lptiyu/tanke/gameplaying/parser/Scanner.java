package com.lptiyu.tanke.gameplaying.parser;

import android.util.Log;

/**
 * author:wamcs
 * date:2016/7/7
 * email:kaili@hustunique.com
 */
public class Scanner {

  public DomNode scan(String text) throws Throwable {
    String content = check(text);
    DomNode root = new DomNode();
    root.setRank(Tag.FIRST_RANK);
    root.setTag(Tag.ROOT_TAG);
    DomNode temp = root;
    String tempContent = null;
    int currentPosition = 0;
    int oldPosition = 0;
    int currentRank;
    for (char c : content.toCharArray()) {
      switch (c) {
        case '<':
          if (oldPosition != currentPosition) {
            tempContent = content.substring(oldPosition + 1, currentPosition);
          }
          oldPosition = currentPosition;
          break;
        case '/':
          if (content.charAt(oldPosition) != '<'){
            break;
          }
          oldPosition = currentPosition;
          break;
        case '>':

          String tag = content.substring(oldPosition + 1, currentPosition);
          currentRank = Tag.checkTag(tag);
          //ignore root tag
          if (currentRank == Tag.FIRST_RANK) {
            oldPosition = currentPosition;
            break;
          }

          if (content.charAt(oldPosition) == '/') {
            if (temp.getTag().equals(tag)) {
              temp.setContent(tempContent);

            } else if (temp.getFather().getTag().equals(tag)) {
              temp = temp.getFather();
            } else {

              throw new Throwable("tag is not fit,current tag is " + tag + ",temp tag is " + temp.getTag());
            }
          } else {
            if (currentRank > temp.getRank()) {
              DomNode node = new DomNode();
              node.setRank(currentRank);
              node.setTag(tag);
              node.setFather(temp);
              temp.addChildren(node);
              temp = node;
            } else if (currentRank == temp.getRank()) {
              DomNode node = new DomNode();
              node.setRank(currentRank);
              node.setTag(tag);
              node.setFather(temp.getFather());
              temp.getFather().addChildren(node);
              temp = node;

            } else {
              throw new Throwable("relation between tag " + tag + " and tag " + temp.getTag() + " is error");
            }

          }
          oldPosition = currentPosition;
          break;
        default:
          break;
      }
      currentPosition++;
    }

    return root;
  }


  private String check(String content) throws Throwable {
    StringBuilder builder = new StringBuilder();
    String temp = null;
    int tagNumber = 0;
    int currentPosition = 0;
    int oldPosition = 0;
    int lastRank = Tag.FIRST_RANK;
    int currentRank;
    for (char c : content.toCharArray()) {
      switch (c) {
        case '<':
          if (oldPosition != currentPosition) {
            temp = content.substring(oldPosition + 1, currentPosition);
          }
          oldPosition = currentPosition;
          tagNumber++;
          break;
        case '/':
          if (content.charAt(oldPosition) != '<'){
            break;
          }
          if (currentPosition - oldPosition != 1) {
            throw new Throwable("grammar error,position is " + currentPosition);
          }
          oldPosition = currentPosition;
          break;
        case '>':

          if (oldPosition + 1 > currentPosition) {
            throw new Throwable("Tag is null or Grammar is error,position is " + currentPosition);
          }
          String tag = content.substring(oldPosition + 1, currentPosition);
          currentRank = Tag.checkTag(tag);
          if (currentRank == Tag.NOT_EXIST) {
            throw new Throwable("Tag is error,position is" + currentPosition);
          }

          if (Math.abs(currentRank - lastRank) == 1) {
            builder.append("<n>").append(temp).append("</n>");
            if (content.charAt(oldPosition) == '/') {
              tag = "/" + tag;
            }
            tag = "<" + tag + ">";
            builder.append(tag);

          } else {

            if (content.charAt(oldPosition) == '/') {
              tag = "/" + tag;
              if (currentRank != Tag.FIRST_RANK) {
                builder.append(temp);
              }
            }

            tag = "<" + tag + ">";
            builder.append(tag);
          }
          oldPosition = currentPosition;
          lastRank = currentRank;
          break;
        default:
          break;
      }
      currentPosition++;
    }

    if (tagNumber % 2 != 0) {
      throw new Throwable("tag is not odd");
    }

    Log.d("lk", builder.toString());
    return builder.toString();

  }


}
