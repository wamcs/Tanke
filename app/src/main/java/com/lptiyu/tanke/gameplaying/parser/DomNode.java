package com.lptiyu.tanke.gameplaying.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * author:wamcs
 * date:2016/7/7
 * email:kaili@hustunique.com
 */
public class DomNode {

  private String tag;
  private String content;
  private List<DomNode> children;
  private DomNode father;
  private int rank;

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public DomNode getFather() {
    return father;
  }

  public void setFather(DomNode father) {
    this.father = father;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<DomNode> getChildren() {
    return children;
  }

  public void addChildren(DomNode child){
    if (null == children){
      children = new ArrayList<>();
    }
    children.add(child);
  }


}
