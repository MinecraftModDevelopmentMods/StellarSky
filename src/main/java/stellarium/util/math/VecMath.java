package stellarium.util.math;

import sciapi.api.temporaries.Temporal;
import sciapi.api.value.IValRef;
import sciapi.api.value.IValue;
import sciapi.api.value.STempRef;
import sciapi.api.value.absalg.IVectorSpace;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVecSet;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.numerics.DDouble;
import sciapi.api.value.numerics.DDoubleSet;
import sciapi.api.value.numerics.IReal;
import sciapi.api.value.numerics.NumMath;
import sciapi.api.value.util.BOp;
import sciapi.api.value.util.VOp;

public class VecMath {
	
	/** Gives Added value for EVector. */
	@Temporal
	public static IValRef<EVector> add(IValRef<EVector> par1, IValRef<EVector> par2)
	{
		return BOp.add(par1, par2);
	}
	
	/** Gives Subtracted value for EVector. */
	@Temporal
	public static IValRef<EVector> sub(IValRef<EVector> par1, IValRef<EVector> par2)
	{
		return BOp.sub(par1, par2);
	}
	
	
	/**
	 * Gives Multiplicated value for EVector.
	 * */
	@Temporal
	public static IValRef<EVector> mult(IValRef<IReal> par1, IValRef<EVector> par2)
	{
		return VOp.mult(par1, par2);
	}
	
	/**
	 * Gives Divided value for EVector.
	 * */
	@Temporal
	public static IValRef<EVector> div(IValRef<IReal> par1, IValRef<EVector> par2)
	{
		return VOp.div(par1, par2);
	}
	
	/**
	 * Gives Multiplicated value for EVector.
	 * */
	@Temporal
	public static IValRef<EVector> mult(double par1, IValRef<EVector> par2)
	{
		STempRef<DDouble> ref = DDoubleSet.ins.getSTemp();
		ref.getVal().set(par1);
		return VOp.mult(ref, par2);
	}
	
	/**
	 * Gives Divided value for EVector.
	 * */
	@Temporal
	public static IValRef<EVector> div(double par1, IValRef<EVector> par2)
	{
		STempRef<DDouble> ref = DDoubleSet.ins.getSTemp();
		ref.getVal().set(par1);
		return VOp.div(ref, par2);
	}
	
	
	/**
	 * Gives Dot product of Euclidian Vectors.
	 * */
	@Temporal
	public static IValRef<IReal> dot(IValRef<EVector> par1, IValRef<EVector> par2)
	{
		return VOp.dot(par1, par2);
	}

	/**
	 * Gives Normalized EVector.
	 * */
	@Temporal
	public static IValRef<EVector> normalize(IValRef<EVector> par)
	{
		return VOp.normalize(par);
	}
	
	@Temporal
	public static IValRef<IReal> size2(IValRef<EVector> par)
	{
		return VOp.size2(par);
	}
	
	@Temporal
	public static IValRef<IReal> size(IValRef<EVector> par)
	{
		return NumMath.sqrt.calc(size2(par));
	}
	
	public static IReal getCoord(IValRef<EVector> par, int N)
	{
		return par.getVal().getCoord(N);
	}
	
	public static int getDimension(IValRef<EVector> par)
	{
		return par.getVal().getDimension();
	}
	
	/**Projection of target to Polar plane*/
	public static final IValRef<EVector> Projection(IValRef<EVector> pol, IValRef<EVector> tar){
		return VecMath.sub(tar, VecMath.mult(VecMath.dot(pol,tar), pol));
	}
	
	
	public static double getX(IValRef<EVector> par)
	{
		return getCoord(par, 0).asDouble();
	}
	
	public static double getY(IValRef<EVector> par)
	{
		return getCoord(par, 1).asDouble();
	}
	
	public static double getZ(IValRef<EVector> par)
	{
		return getCoord(par, 2).asDouble();
	}
}
