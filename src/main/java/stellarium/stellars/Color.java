package stellarium.stellars;

public class Color {
	public short r;

	public short g;

	public short b;
	
	public Color(short pr, short pg, short pb){
		r = pr;
		g = pg;
		b = pb;
	}
	
	public Color(){
		r = 255;
		g = 255;
		b = 255;
	}
	
	static short color[]=
		{155,	178,	255,
	158,	181,	255,
	163,	185,	255,
	170,	191,	255,
	178,	197,	255,
	187,	204,	255,
	196,	210,	255,
	204,	216,	255,
	211,	221,	255,
	218,	226,	255,
	223,	229,	255,
	228,	233,	255,
	233,	236,	255,
	238,	239,	255,
	243,	242,	255,
	248,	246,	255,
	254,	249,	255,
	255,	249,	251,
	255,	247,	245,
	255,	245,	239,
	255,	243,	234,
	255,	241,	229,
	255,	239,	224,
	255,	237,	219,
	255,	235,	214,
	255,	233,	210,
	255,	232,	206,
	255,	230,	202,
	255,	229,	198,
	255,	227,	195,
	255,	226,	191,
	255,	224,	187,
	255,	223,	184,
	255,	221,	180,
	255,	219,	176,
	255,	218,	173,
	255,	216,	169,
	255,	214,	165,
	255,	213,	161,
	255,	210,	156,
	255,	208,	150,
	255,	204,	143,
	255,	200,	133,
	255,	193,	120,
	255,	183,	101,
	255,	169,	75,
	255,	149,	35,
	255,	123,	0,
	255,	82,	0};
	
	public static final Color GetColor(double B_V){
		int k=(int)((B_V+0.4)*20.0);
		if(k<0) k=0;
		if(k>48) k=48;
		
		Color c=new Color();
		c.r=(short) ((color[k*3]+255)/2);
		c.g=(short) ((color[k*3+1]+255)/2);
		c.b=(short) ((color[k*3+2]+255)/2);
		
		return c;
	}
	
	public static final Color GetColor(float B_V){
		int k=(int)((B_V+0.4)*20.0);
		if(k<0) k=0;
		if(k>48) k=48;
		
		Color c=new Color();
		c.r=(short) ((color[k*3]+255)/2);
		c.g=(short) ((color[k*3+1]+255)/2);
		c.b=(short) ((color[k*3+2]+255)/2);
		
		return c;
	}
}
