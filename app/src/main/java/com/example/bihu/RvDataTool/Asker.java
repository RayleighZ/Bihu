package com.example.bihu.RvDataTool;

public class Asker {
    public String name;
    public String setence;
    public String title;
    public String cai;
    public String zan;
    public String huifu;
    public String ImageUrl;
    public String head;
    public String time;
    public int id;
    public int authorId;
    public boolean is_favorite;
    public boolean is_exciting;
    public boolean is_naive;


    public Asker(String name, String setence, String title, String cai, String zan, String huifu, int id, String imageUrl, String head, boolean is_exciting, boolean is_naive,boolean is_favorite,String time,int authorId) {
        this.name = name;
        this.setence = setence;
        this.title = title;
        this.cai = cai;
        this.zan = zan;
        this.huifu = huifu;
        this.id = id;
        this.ImageUrl = imageUrl;
        this.head = head;
        this.is_exciting = is_exciting;
        this.is_naive = is_naive;
        this.is_favorite = is_favorite;
        this.time = time;
        this.authorId = authorId;
    }
}
