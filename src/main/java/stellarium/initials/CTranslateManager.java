package stellarium.initials;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import stellarium.stellars.cbody.CBody;
import stellarium.stellars.cbody.NonStarBody;

public class CTranslateManager
{
  public static Map<String, String> translatemap = new HashMap();
  public static final String Dist = "";
  public static final String Lon = "";
  public static final String Lat = "";

  public static String[] Translate(byte[] config)
  {
    int i = 0;
    int num = config.length;
    ArrayList buf = new ArrayList();
    ArrayList buffer = new ArrayList();
    String[] cworld = new String[0];
    boolean InStr = false; boolean InBrace = false;
    while (i < num)
    {
      boolean Valid = true;

      if (config[i] == '"') {
        InStr = !InStr;
        Valid = false;
      }

      if (!InStr) {
        Valid = false;
        if (config[i] == '{')
          InBrace = true;
        else if (config[i] == '}')
          InBrace = false;
        else if ((config[i] == '\n') || (config[i] == ':'))
          if (!InBrace) {
            byte[] b = new byte[buf.size()];
            for (int j = 0; j < buf.size(); j++)
              b[j] = ((Byte)buf.get(j)).byteValue();
            String bs = new String(b, 0, b.length);
            bs = (String)translatemap.get(bs);
            if ((!bs.equals("")) && (!bs.startsWith("#")))
              buffer.add(bs);
            buf.clear();
          } else {
            Valid = true;
          }
      }
      if ((InStr) || ((Valid) && (config[i] != 32))) {
        buf.add(Byte.valueOf(config[i]));
      }
    }
    cworld = (String[])buffer.toArray(cworld);
    return cworld;
  }

  public static void AddTranslation(String pre, String post) {
    if ((translatemap.containsKey(pre)) && (!((String)translatemap.get(pre)).equals("post")))
    {
      String exc = "Duplicate Translation: " + pre + "to " + post + " Detected!\n" + 
        "Duplicate Translation: " + pre + "to " + (String)translatemap.get(pre) + " Detected!";
      throw new RegistrationException(exc);
    }
    translatemap.put(pre, post);
  }

  public static void TranslationBase() {
    AddTranslation("@Orbit", "@");
    AddTranslation("+CBody", "+");
    AddTranslation("Type", "T");
    AddTranslation("Parent", "P");
    AddTranslation("Name", "N");
    AddTranslation("Virtual", "V");
    AddTranslation("Mass", "M");
//    AddTranslation("Resonant", CBody.Resonance);
//    AddTranslation("Pole", CBody.PPole);
//    AddTranslation("PMAngle", CBody.PPMAngle);
//    AddTranslation("Prec", CBody.PPrec);
//    AddTranslation("Rot", CBody.PRot);
//    AddTranslation("Radius", CBody.PRadius);
//    AddTranslation("Albedo", NonStarBody.PAlbedo);

    AddTranslation("Distance", "");
    AddTranslation("Longitude", "");
    AddTranslation("Lattitude", "");
  }
}