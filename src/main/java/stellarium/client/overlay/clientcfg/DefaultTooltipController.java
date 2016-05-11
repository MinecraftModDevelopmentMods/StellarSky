package stellarium.client.overlay.clientcfg;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.IFontHelper;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.dynamic.tooltip.GuiHasTooltip;
import stellarapi.lib.gui.dynamic.tooltip.ITooltipController;
import stellarapi.lib.gui.dynamic.tooltip.ITooltipElementController;
import stellarapi.lib.gui.dynamic.tooltip.StringFormat;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.font.ModelFont;
import stellarapi.lib.gui.model.font.TextStyle;

public class DefaultTooltipController implements ITooltipController, ICfgTooltipHandler {
	
	/** Default format, exist as separator. */
	public static final String formatDefault = "\\\n";
	
	/** Title format */
	public static final String formatTitle = "\\\t";
	
	/** Context format */
	public static final String formatContext = "\\\b";
	
	/** Context format */
	public static final String formatWarning = "\\w";
	
	public static final String separatorCheckRegex = String.format("(%s|%s|%s|%s)",
			Pattern.quote(formatDefault), Pattern.quote(formatTitle),
			Pattern.quote(formatContext), Pattern.quote(formatWarning));
	public static final Pattern separatorSplitPattern = Pattern.compile(
			String.format("(?=%s)", separatorCheckRegex));
	public static final Pattern separatorCheckPattern = Pattern.compile(separatorCheckRegex);
	public static final Pattern sentenceEndPattern = Pattern.compile("([^0-9](\\.|!|\\?))");
	private static final Pattern formatPattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
	
	private ModelFont font = new ModelFont(false);
	private ModelFont fontBold = new ModelFont(false) {
		public float getStringHeight() {
			return super.getStringHeight() * 1.2f;
		}
	};
	
	private TextStyle style = new TextStyle();

	private GuiHasTooltip gui = new GuiHasTooltip();
	
	public DefaultTooltipController() {
		fontBold.setStyle(new TextStyle().setBold(true));
	}
	
	public GuiElement generateGui() {
		return new GuiElement<ITooltipController>(this.gui, this);
	}
	
	public void setWrappedGui(GuiElement wrapped) {
		gui.setWrappedGui(wrapped);
	}

	@Override
	public ITooltipElementController defaultTooltip(ConfigCategory category) {
		return new DefTooltipElementController(formatTitle + category.getLanguagekey() +
				formatContext + category.getLanguagekey() + CfgConstants.SUFFIX_TOOLTIP);
	}

	@Override
	public ITooltipElementController defaultTooltip(Property property) {
		return new DefTooltipElementController(formatTitle + property.getLanguageKey() +
				formatContext + property.getLanguageKey() + CfgConstants.SUFFIX_TOOLTIP);
	}
	
	public class DefTooltipElementController implements ITooltipElementController {
		private String tooltip;
		
		public DefTooltipElementController(String tooltip) {
			this.tooltip = tooltip;
		}

		@Override
		public boolean canDisplayTooltip() {
			return true;
		}

		@Override
		public int getTooltipDisplayWaitTime() {
			return CfgConstants.WAIT_TIME_SLOW_TOOLTIP;
		}

		@Override
		public StringFormat getTooltipInfo(float ratioX, float ratioY) {
			return new StringFormat(this.tooltip);
		}
	}

	@Override
	public GuiElement wrapElement(GuiElement element, String tooltip) {
		return gui.wrapElement(element, tooltip);
	}

	@Override
	public GuiElement wrapElement(GuiElement element, ITooltipElementController controller) {
		return gui.wrapElement(element, controller);
	}

	@Override
	public boolean hasClip() {
		return false;
	}

	@Override
	public String setupBackground(StringFormat info, IRenderer renderer) {
		renderer.bindModel(ModelSimpleRect.getInstance());
		renderer.pushSettingTillNextRender();
		renderer.color(0.2f, 0.2f, 0.2f, 0.5f);
		
		return "";
	}

	@Override
	public List<String> getLineContext(StringFormat info) {
		List<String> lines = Lists.newArrayList();

		List<Object> list = Arrays.asList(info.getArguments());
		Object[] inspect = new Object[list.size()];
		Arrays.fill(inspect, "%f");
		
		String[] separated = separatorSplitPattern.split(info.getMain());
		int numArgumentsUsed = 0;

		for(String current : separated) {
			String format, localized, prefix = "";
			Matcher formatMatcher = separatorCheckPattern.matcher(current);
			if(formatMatcher.find()) {
				format = formatMatcher.group();
				current = current.substring(formatMatcher.end());
			} else format = formatDefault;
			
			if(format.equals(formatContext))
				prefix = " ";
			
			Matcher formattables = formatPattern.matcher(I18n.format(current, inspect));
			if(formattables.find()) {
				while(formattables.find());
				
				int count = formattables.groupCount();
				if(numArgumentsUsed + count > list.size())
					throw new IndexOutOfBoundsException(
							String.format("Too less arguments %s for %s",
									info.getArguments(), info.getMain()));
				localized = I18n.format(current,
						list.subList(numArgumentsUsed, numArgumentsUsed + count).toArray());
				numArgumentsUsed += count;
			} else  localized = I18n.format(current);
			Matcher sentenceMatcher = sentenceEndPattern.matcher(localized);
			int currentStartPointer = 0;
			int nextStartPointer;

			while(currentStartPointer < localized.length()) {
				String line = font.trimStringToWidth(
						localized.substring(currentStartPointer), 250);

				if(sentenceMatcher.find(currentStartPointer) && sentenceMatcher.end() <= currentStartPointer + line.length())
					nextStartPointer = sentenceMatcher.end();
				else if(currentStartPointer + line.length() >= localized.length()) {
					nextStartPointer = localized.length();
				} else {
					int index = line.lastIndexOf(" ");
					nextStartPointer = currentStartPointer + (index != -1? (index + 1) : line.length());
				}
				
				lines.add(format + localized.substring(currentStartPointer, nextStartPointer));
				currentStartPointer = nextStartPointer;
			}
		}
		
		return lines;
	}
	
	@Override
	public String toRenderableText(String context) {
		Matcher formatMatcher = separatorCheckPattern.matcher(context);
		if(formatMatcher.find()) {
			String format = formatMatcher.group();
			String prefix = format.equals(formatContext)? " " : "";
			return prefix + context.substring(formatMatcher.end());
		} else return context;
	}
	
	@Override
	public IFontHelper lineSpecificFont(String lineContext) {
		Matcher formatMatcher = separatorCheckPattern.matcher(lineContext);
		if(formatMatcher.find()) {
			String format = formatMatcher.group();
			if(lineContext.startsWith(formatTitle))
				return this.fontBold;
		}
		return this.font;
	}

	@Override
	public void setupTooltip(String context, IRenderer renderer) {
		style.reset();
		if(context.startsWith(formatTitle)) {
			fontBold.setColor(1.0f, 1.0f, 0.6f, 1.0f);
			renderer.bindModel(this.fontBold);
		} else if(context.startsWith(formatContext)) {
			font.setColor(0.85f, 0.85f, 0.85f, 1.0f);
			renderer.bindModel(this.font);
		} else if(context.startsWith(formatWarning)) {
			font.setColor(0.9f, 0.5f, 0.5f, 1.0f);
			renderer.bindModel(this.font);
		} else {
			font.setColor(0.9f, 0.9f, 0.9f, 1.0f);
			renderer.bindModel(this.font);
		}
	}

	@Override
	public float getSpacingX() {
		return 2.0f;
	}

	@Override
	public float getSpacingY() {
		return 2.0f;
	}

}
