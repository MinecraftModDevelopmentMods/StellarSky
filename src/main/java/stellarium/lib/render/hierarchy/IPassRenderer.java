package stellarium.lib.render.hierarchy;

public interface IPassRenderer<Pass, ResRCI> {
	public void renderPass(Object model, Pass pass, ResRCI resInfo);
}