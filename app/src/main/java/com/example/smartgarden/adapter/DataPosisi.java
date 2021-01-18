package com.example.smartgarden.adapter;

public class DataPosisi {
    private int x = 0;
    private int y = 0;

    public boolean update(int x, int y){
        this.x = x;
        this.y = y;
        return true;
    }
    public String up(){
        if(this.y > 0) return "Y"+(--this.y);
        return "Y0";
    }
    public String down(){
        return "Y"+(++this.y);
    }
    public String right(){
        return "X"+(++this.x);
    }
    public String left(){
        if(this.x > 0) return "X"+(--this.x);
        return "X0";
    }
    public String go(int x, int y){
        this.x = x;
        this.y = y;
        return "X"+x+" Y"+y;
    }
    public String goByGrid(int x, int y, int columnScale, int rowScale){
        int xDim = 115;
        int posx = (int) (Math.round(((float) xDim /columnScale)*.5) + Math.round(x*((float) xDim /columnScale)));
        int yDim = 24;
        int posy = (int) (Math.round(((float) yDim /rowScale)*.5) + Math.round(y*((float) yDim /rowScale)));
        this.x = posx;
        this.y = posy;
        return "X"+ posx + " Y" + posy;
    }
    public String toString(){
        return "X_POS_CM: " + this.x + " Y_POS_CM: " + this.y;
    }
}
