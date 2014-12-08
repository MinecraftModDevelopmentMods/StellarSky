package stellarium.initials;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import stellarium.stellars.StellarManager;
import stellarium.stellars.cbody.AiryCBody;
import stellarium.stellars.cbody.CBody;
import stellarium.stellars.cbody.IAstCBody;
import stellarium.stellars.cbody.RockyCBody;
import stellarium.stellars.cbody.SAstCBody;
import stellarium.stellars.cbody.StarBody;
import stellarium.stellars.orbit.Orbit;
import stellarium.stellars.orbit.OrbitSt;
import stellarium.stellars.orbit.Orbitse;

public class CConstructManager
{
  public static final String Orb = "@";
  public static final String Type = "T";
  public static final String Name = "N";
  public static final String Parent = "P";
  public static Map<String, Class<? extends Orbit>> Types = new HashMap();
  public static Map<String, Class<? extends CBody>> CBTypes = new HashMap();

  public OrbitSt Construct(String[] CWorld)
  {
    
    ArrayList Buffer = new ArrayList();
    String[] ConOrb = null;
    OrbitSt CSystem = new OrbitSt();
    int num = CWorld.length;
    int i = 0;
    boolean IsCreated = false;

    Orbit obj = null;

    while (i < num)
      if ((CWorld[i].equals("@")) && (obj != null)) {
        ConOrb = (String[])Buffer.toArray(ConOrb);
        obj.PreConstruct();
        obj.Construct(ConOrb);
        obj.PostConstruct();
        Buffer.clear();
        IsCreated = false;
        i++;
      }
      else if ((CWorld[i].equals("P")) && (obj != CSystem)) {
        i++;
        if (!IsCreated) {
          IllegalForm("Parent Property Must Be After Type Property.");
        }
        Orbit par = CSystem.Search(CWorld[i]);
        par.addSatellite(obj);
        obj.ParOrbit = par;
        i++;
      }
      else if (CWorld[i].equals("N")) {
        i++;
        if (!IsCreated) {
          IllegalForm("Name Property Must Be After Type Property.");
        }
        obj.Name = CWorld[i];
        i++;
      }
      else if ((!IsCreated) && (CWorld[i].equals("T"))) {
        i++;
        if (!Types.containsKey(CWorld[i]))
          IllegalForm("Orbit Type " + CWorld[i] + " Not Exist!");
        Class orbtype = (Class)Types.get(CWorld[i]);
        if (orbtype == CSystem.getClass())
          obj = CSystem;
        else
          try {
            obj = (Orbit)orbtype.newInstance();
          } catch (InstantiationException e) {
            IllegalForm("Invalid Orbit Type " + CWorld[i]);
          } catch (IllegalAccessException e) {
            IllegalForm("Invalid Orbit Type " + CWorld[i]);
          }
        IsCreated = true;
        i++;
      }
      else {
        Buffer.add(CWorld[i]);
        i++;
      }
    
	return CSystem;
    
      
  }

  public void PreRegister()
  {
    this.RegisterOrbit("S", OrbitSt.class);
    this.RegisterOrbit("s", Orbitse.class);

    this.RegisterCBody("t", StarBody.class);
    this.RegisterCBody("A", AiryCBody.class);
    this.RegisterCBody("R", RockyCBody.class);
    this.RegisterCBody("I", IAstCBody.class);
    this.RegisterCBody("S", SAstCBody.class);
  }

  
  public static void RegisterOrbit(String typename, Class<? extends Orbit> cl){
    if (Types.containsKey(typename))
     IllegalReg("Duplicate Orbit Type: " + typename + " Detected!");
    Types.put(typename, cl);
    try {
      ((Orbit)cl.newInstance()).RegisterOrbit();
    } catch (InstantiationException e) {
      IllegalReg("Invalid Orbit Type " + typename);
    } catch (IllegalAccessException e) {
      IllegalReg("Invalid Orbit Type " + typename);
    }
  }

  public static void RegisterCBody(String typename, Class<? extends CBody> cl){
    if (CBTypes.containsKey(typename))
      IllegalReg("Duplicate Body Type: " + typename + " Detected!");
    CBTypes.put(typename, cl);
    try {
      ((CBody)cl.newInstance()).RegisterCBody();
    } catch (InstantiationException e) {
      IllegalReg("Invalid Body Type " + typename);
    } catch (IllegalAccessException e) {
      IllegalReg("Invalid Body Type " + typename);
    }
  }

  public void RegisterCBodyWithWorld()
  {
  }
  
  public static void IllegalReg(String Content) throws RegistrationException{
	  throw new RegistrationException(Content);
  }

  public static void IllegalForm(String Content)
  {
    String con = "Illegal Configuration Form: " + Content;
    
    throw new CreationException(con);
  }
}