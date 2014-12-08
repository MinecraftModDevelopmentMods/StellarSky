package stellarium.viewrender.render;

import net.minecraft.client.Minecraft;
import sciapi.api.value.euclidian.EVector;
import stellarium.stellars.cbody.*;
import stellarium.stellars.local.LocalCValue;
import stellarium.util.math.*;
import stellarium.viewrender.viewer.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StellarRenderManager {
	
	public Viewer viewer;
	
	public StellarRenders eye = new StellarRenders();
	boolean CCDMode = false;
	public StellarRenders CCD;
	
	public long beforetick = -1;
	
	public void Init(Viewer v){
		viewer=v;
	}
	
	public void EnableCCD(){
		CCDMode = true;
		CCD = new StellarRenders();
	}
	
	public void DisableCCD(){
		CCDMode = false;
		CCD = null;
	}
	
	public void Reset(EVector v){
		if(!CCDMode)
			eye.Reset(v);
		else
			CCD.Reset(v);
	}
	
	public void PreRenderAdd(){
		if(viewer.CurVp.HostCBody!=null) {
			LocalCValue lcv=viewer.CurVp.mapotoln.get(viewer.CurVp.HostCBody);
			RHost host = viewer.CurVp.HostCBody.RenderHost(lcv)
					.SetHor(viewer.CurVp.Zen, viewer.CurVp.North, viewer.CurVp.East);
			if(!CCDMode)
				eye.RenderHost(host);
			else
				CCD.RenderHost(host);
		}
		
//		if(!CCDMode)
//			viewer.bgm.AddBgToRender(eye);
//		else
//			viewer.bgm.AddBgToRender(CCD);
	}
	
	public void AddCBodyforRender(CBody body, double part){
		LocalCValue lcv=viewer.CurVp.mapotoln.get(body.theOrbit);
		if(viewer.CurVp.HostCBody != null && body == viewer.CurVp.HostCBody){
			return;
		}
		
		if(!CCDMode)
			eye.RenderObj(body.RenderBody(lcv, eye.Res));
		else
			CCD.RenderObj(body.RenderBody(lcv, CCD.Res));
	}
	
	public void PostRenderAdd(){
		if(!CCDMode)
			eye.PostRenderAdd();
		else
			CCD.PostRenderAdd();
	}
	
	//Actual Rendering Function
	public void Render(){
		if(!CCDMode)
			eye.Render(IsRenderUpdate(eye.Res));
		else
			CCD.Render(IsRenderUpdate(CCD.Res));
	}
	
	protected boolean IsRenderUpdate(double res){
		long nowtick = Minecraft.getMinecraft().theWorld.getWorldTime();
		if(nowtick == beforetick) return false;
		beforetick = nowtick;
		return nowtick % (500.0 / res) == 0;
	}

}
