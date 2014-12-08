package stellarium.stellars.orbit;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import stellarium.initials.CCertificateHelper;
import stellarium.stellars.cbody.CBody;
import stellarium.util.UpDouble;
import stellarium.util.math.*;

public abstract class OrbitMv extends Orbit {
	
	public UpDouble i=new UpDouble(), Om=new UpDouble();
	
	public EVector Pol = new EVector(3);
	
	public double Hill_Radius;
	
	public void PreCertificate(){
		super.PreCertificate();
		HillRadius();
	}
	
	public void Certificate(){
		CCertificateHelper cch = null;
		super.Certificate();
		if((!ParOrbit.IsVirtual) && this.Mass < ParOrbit.Mass * cch.SatMassLimit)
			cch.Unstable("Mass of "+this.Name+" Is too big compared to the parent "+ParOrbit.Name+"!");
	}
	
	@SideOnly(Side.SERVER)
	@Override
	public void Update(double yr) {
		UpdateOrbitalElements(yr);
		UpdateEcRPos();
		ParEcRPos();
		UpdatePole();
		super.Update(yr);
	}

	@SideOnly(Side.SERVER)
	protected abstract void UpdateOrbitalElements(double yr);
	
	@SideOnly(Side.SERVER)
	protected abstract void UpdateEcRPos();
	
	public abstract double GetAvgRot();
	
	@SideOnly(Side.SERVER)
	protected void ParEcRPos(){
		Pos.set(VecMath.add(Pos, ParOrbit.Pos));
	}
	
	Rotate ri = new Rotate('X'), rom = new Rotate('Z');
	
	@SideOnly(Side.SERVER)
	protected void UpdatePole(){
		Pol.set(0.0, 0.0, 1.0);
		ri.setRAngle(-Spmath.Radians(i.val0));
		rom.setRAngle(-Spmath.Radians(Om.val0));
		Pol.set((IValRef)rom.transform(ri.transform((IValRef)Pol)));
	}
	
	
	@SideOnly(Side.SERVER)
	abstract protected void HillRadius();
}
