package com.worldriskclock.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.view.*;
import java.util.*;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(9,13,22));
        getWindow().setNavigationBarColor(Color.rgb(9,13,22));
        setContentView(new RiskClockView());
    }

    private class RiskClockView extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int page = 0;
        private final int bg = Color.rgb(9,13,22), card = Color.rgb(18,25,39), text = Color.WHITE,
                muted = Color.rgb(164,174,193), red = Color.rgb(244,67,54), amber = Color.rgb(255,183,77), green = Color.rgb(78,201,126);

        private final Risk[] risks = {
                new Risk("Nuclear", 78, 0.30), new Risk("War / Geopolitics", 72, 0.25),
                new Risk("AI / Technology", 58, 0.15), new Risk("Climate", 64, 0.15),
                new Risk("Global Instability", 69, 0.15)
        };

        private final Country[] countries = {
                new Country("Russia", 91), new Country("Ukraine", 88), new Country("Israel", 85),
                new Country("Iran", 84), new Country("North Korea", 83), new Country("United States", 76),
                new Country("China", 75), new Country("Taiwan", 74), new Country("Pakistan", 72),
                new Country("India", 68), new Country("Syria", 67), new Country("Lebanon", 65),
                new Country("Yemen", 63), new Country("Sudan", 62), new Country("Belarus", 60)
        };

        RiskClockView() { super(MainActivity.this); setBackground(new ColorDrawable(bg)); p.setTypeface(Typeface.create("sans", Typeface.NORMAL)); }
        private float dp(float v){ return v * getResources().getDisplayMetrics().density; }
        private void txt(Canvas c,String s,float x,float y,float size,int color,boolean bold){p.setColor(color);p.setTextSize(dp(size));p.setTypeface(Typeface.create("sans",bold?Typeface.BOLD:Typeface.NORMAL));c.drawText(s,x,y,p);}        
        private void round(Canvas c,float l,float t,float r,float b,float rad,int color){p.setColor(color);c.drawRoundRect(l,t,r,b,dp(rad),dp(rad),p);}        
        private int score(){double s=0; for(Risk r:risks)s+=r.value*r.weight; return (int)Math.round(s);}        
        private String clockText(){ int s=score(); int seconds=(int)Math.round(720*(s/100.0)); int mins=seconds/60; int secs=seconds%60; return String.format(Locale.UK,"%d:%02d to midnight",12-mins,60-secs==60?0:60-secs); }

        @Override protected void onDraw(Canvas c){ super.onDraw(c); if(page==0)home(c); else if(page==1)countries(c); else methodology(c); nav(c); }

        private void header(Canvas c,String subtitle){ txt(c,"WORLD RISK CLOCK",dp(20),dp(38),24,text,true); txt(c,subtitle,dp(20),dp(60),12,muted,false); }
        private void home(Canvas c){
            header(c,"Demo risk model • no live data feed yet");
            float cx=getWidth()/2f, cy=dp(190), rad=dp(98); p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(12));p.setColor(Color.rgb(43,52,68));c.drawCircle(cx,cy,rad,p);p.setColor(red);RectF a=new RectF(cx-rad,cy-rad,cx+rad,cy+rad);c.drawArc(a,-90,360*score()/100f,false,p);p.setStyle(Paint.Style.FILL);
            txt(c,String.valueOf(score()),cx-dp(35),cy+dp(10),48,text,true);txt(c,"/100",cx+dp(30),cy+dp(10),16,muted,false);
            txt(c,clockText(),cx-dp(72),cy+dp(38),13,amber,true);
            float y=dp(318); for(Risk r:risks){ round(c,dp(16),y,getWidth()-dp(16),y+dp(58),14,card);txt(c,r.name,dp(30),y+dp(24),14,text,true);txt(c,r.value+"",getWidth()-dp(54),y+dp(24),14,text,true); float barL=dp(30),barR=getWidth()-dp(30),barY=y+dp(39);round(c,barL,barY,barR,barY+dp(7),4,Color.rgb(43,52,68));round(c,barL,barY,barL+(barR-barL)*r.value/100f,barY+dp(7),4,r.value>=75?red:(r.value>=60?amber:green));y+=dp(66);}
            round(c,dp(16),y,getWidth()-dp(16),y+dp(92),14,card);txt(c,"WHY THE CLOCK MOVED",dp(30),y+dp(25),13,amber,true);txt(c,"Current prototype weights elevated nuclear and war risk.",dp(30),y+dp(48),12,text,false);txt(c,"Demo only — live sourced explanations will replace this.",dp(30),y+dp(68),12,muted,false);
        }
        private void countries(Canvas c){
            header(c,"15-country demo watchlist • editable starter data");float y=dp(88);int i=1;for(Country k:countries){round(c,dp(16),y,getWidth()-dp(16),y+dp(42),12,card);txt(c,String.valueOf(i),dp(28),y+dp(27),12,muted,true);txt(c,k.name,dp(54),y+dp(27),14,text,true);txt(c,k.score+"",getWidth()-dp(56),y+dp(27),14,k.score>=80?red:(k.score>=65?amber:green),true);y+=dp(48);i++;}
        }
        private void methodology(Canvas c){
            header(c,"How the score is calculated");float y=dp(96);round(c,dp(16),y,getWidth()-dp(16),y+dp(122),14,card);txt(c,"WEIGHTED GLOBAL SCORE",dp(30),y+dp(28),14,amber,true);txt(c,"Nuclear 30%  •  War 25%  •  AI 15%",dp(30),y+dp(55),13,text,false);txt(c,"Climate 15%  •  Global instability 15%",dp(30),y+dp(80),13,text,false);txt(c,"Score 100 represents maximum modeled danger.",dp(30),y+dp(105),12,muted,false);
            y+=dp(140);round(c,dp(16),y,getWidth()-dp(16),y+dp(166),14,card);txt(c,"PUBLISHING STANDARD",dp(30),y+dp(28),14,amber,true);String[] lines={"• Every score change should cite its source.","• News signals require confidence ratings.","• Scenario Mode must be clearly labeled hypothetical.","• No claim that the clock predicts an actual doomsday.","• Live data feed to be added before public release."};float yy=y+dp(56);for(String l:lines){txt(c,l,dp(30),yy,12,text,false);yy+=dp(22);}            
        }
        private void nav(Canvas c){float h=getHeight(),t=h-dp(70);round(c,dp(12),t,getWidth()-dp(12),h-dp(10),18,Color.rgb(13,19,31));String[] n={"CLOCK","COUNTRIES","METHOD"};for(int i=0;i<3;i++){float x=(i+.5f)*getWidth()/3f;txt(c,n[i],x-dp(i==1?34:25),t+dp(36),11,i==page?amber:muted,true);}}
        @Override public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()==MotionEvent.ACTION_UP && e.getY()>getHeight()-dp(85)){page=Math.min(2,Math.max(0,(int)(e.getX()/(getWidth()/3f))));invalidate();return true;}return true;}
    }
    private static class Risk{String name;int value;double weight;Risk(String n,int v,double w){name=n;value=v;weight=w;}}
    private static class Country{String name;int score;Country(String n,int s){name=n;score=s;}}
}
