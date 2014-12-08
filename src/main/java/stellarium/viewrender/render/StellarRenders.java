package stellarium.viewrender.render;

import java.nio.DoubleBuffer;
import java.util.*;

import org.lwjgl.opengl.GL11;

import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.util.VOp;
import stellarium.stellars.Optics;
import stellarium.stellars.StellarManager;
import stellarium.util.math.*;
import stellarium.viewrender.scope.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.*;

@SideOnly(Side.CLIENT)
public class StellarRenders {
	
	public static double MaxC=10.0;
	public static double MinC=0.001;
	
	private Tessellator tessellator=Tessellator.instance;
	private double bglight, weathereff;
	
	public EVector view;
	public Scope scope;
	
	public double Res;
	protected double Seeing;
	protected double con;
	protected double maxlum;
	
	protected RHost host;
	protected ArrayList<RBase> bgobjs;
	protected LinkedList<RCShape> shapes;
	
	protected int lev;
	
	private int skylist = GL11.glGenLists(1);
	
	private boolean first = true;
	
	public void Reset(EVector v){
		view = v;
		bgobjs.clear();
		shapes.clear();
		maxlum = bglight;
		
	}
	
	public void SetLProp(double blight, double weff){
		bglight = blight;
		weathereff = weff;
	}
	
	public void SetScope(Scope sc){
		scope = sc;
		this.RenderList();
	}
	
	public void RenderHost(RHost h){
		host = h;
		if(host != null && !scope.AO){
			Seeing = host.Seeing * Math.exp(-host.DtoR);
		}
		else Seeing = 0.0;
		
		Res = Math.max(Seeing, scope.Res);
	}
	
	public void RenderObj(RBase obj){
		if(obj instanceof RPointy)
			RenderPointy((RPointy)obj);
		else if(obj instanceof RCShape)
			RenderShape((RCShape)obj);
	}
	
	public void RenderPointy(RPointy pointy){
		if(IsInViewSight(pointy.Pos, 0.0))
			AddBgObj(pointy);
		else pointy = null;
	}
	
	public void RenderDSObj(RDSObj dsobj){
		if(IsInViewSight(dsobj.Pos, dsobj.Size))
			AddBgObj(dsobj);
		else dsobj = null;
	}
	
	public void RenderShape(RCShape shape){
		if(IsInViewSight(shape.Pos, shape.Size))
			AddShape(shape);
		else shape = null;
	}
	
	protected boolean IsInViewSight(EVector pos, double Size){
		double angle;
		
		if(scope.FOV < 2.0)
			angle = Spmath.Radians(Spmath.getD(VOp.size(CrossUtil.cross((IEVector)pos, (IEVector)view))));
		else
			angle = Spmath.Degrees(Math.acos(Spmath.getD(VecMath.dot(pos, view))));
		
		if(angle+Size > scope.FOV/2) return false;
		
		return true;
	}
	
	protected void AddBgObj(RBase bgobj){
		bgobj.sr = this;
		bgobjs.add(bgobj);
		
		maxlum = Math.max(bgobj.Lum, maxlum);
	}
	
	protected void AddShape(RCShape shape){
		shape.sr = this;
		ListIterator<RCShape> ite=shapes.listIterator();
		while(ite.hasNext())
			if(ite.next().Dist > shape.Dist){
				ite.previous();
				ite.add(shape);
			}
		
		maxlum = Math.max(shape.Lum, maxlum);
	}
	
	public void PostRenderAdd() {
		SetCon();
	}
	
	protected void SetCon(){
		con = 1.0 / (maxlum*scope.Conc);
		con = Math.max(con, MinC);
		con = Math.min(con, MaxC);
		con *= scope.Conc;
	}
	
	public void Render(boolean update){
		
		PreRender();
		
		if(update || this.first)
			this.RenderSubList();
		
		GL11.glCallList(skylist);
		
		PostRender();
		
		first = false;
	}
	
	protected void RenderSubList() {
		int i;
		
		CRenderEngine.instance.con = this.con;

        GL11.glNewList(this.skylist, GL11.GL_COMPILE);
        

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ZERO);
		
		ListIterator<RCShape> ite = shapes.listIterator();
		while(ite.hasNext()){
			ite.next().render();
		}
		
		if(host != null)
			host.render();
        
        GL11.glEndList();
	}

	protected void RenderList(){
		int i;
		
		CRenderEngine.instance.con = this.con;

        GL11.glNewList(this.skylist, GL11.GL_COMPILE);
        
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        
        
        
		for(i=0; i<bgobjs.size(); i++)
			bgobjs.get(i).render();
		
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ZERO);
		
		ListIterator<RCShape> ite = shapes.listIterator();
		while(ite.hasNext()){
			ite.next().render();
		}
		
		if(host != null)
			host.render();
        
        GL11.glEndList();
	}

	protected void PreRender(){
		//Horizontal Coordinate to Ecliptic Coordinate
        if(host != null){
        	//Coord hrd = new Coord(host.East, host.North, host.Zen);
        	//Coord ecl = hrd.InverseCoord();
        	
        	//GL11.glMultMatrix(ecl.ToDoubleBuffer());
        	
        	if(this.host instanceof RAtmHost)
        		CRenderEngine.instance.SetAtm((RAtmHost)host);
        	CRenderEngine.instance.SetRes(Res);
        }
	}
	
	protected void PostRender(){
		
	}
	
}
