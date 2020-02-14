package com.example.bihu.RvDataTool;

public class Replyer {
    public String goodnum;
    public String badnum;
    public String contest;
    public String name;
    public String head;
    public String best;
    public String qid;
    public String imageUrl;
    public String time;
    public boolean whetherExciting;
    public boolean whetherNaive;
    public String Token;
    public int id;

    public Replyer(String goodnum, String badnum, String contest, String name, String head, boolean whetherExciting, boolean whetherNaive,String token,int id,String best,String qid,String imageUrl,String Time) {
        this.goodnum = goodnum;
        this.badnum = badnum;
        this.contest = contest;
        this.name = name;
        this.head = head;
        this.whetherExciting = whetherExciting;
        this.whetherNaive = whetherNaive;
        this.Token = token;
        this.id = id;
        this.best = best;
        this.qid = qid;
        this.imageUrl = imageUrl;
        this.time = Time;
    }
}
